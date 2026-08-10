package com.example.data_search.service;

import com.example.data_search.config.ScrapingProperties;
import com.example.data_search.entity.Company;
import com.example.data_search.entity.SearchQueryCache;
import com.example.data_search.repository.CompanyRepository;
import com.example.data_search.repository.SearchQueryCacheRepository;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CompanySearchService {

    private static final Logger log = LoggerFactory.getLogger(CompanySearchService.class);

    private final PoliteHttpClient httpClient;
    private final CompanySearchResultsParser searchParser;
    private final CompanyOverviewParser overviewParser;
    private final OfficersPageParser officersParser;
    private final PscPageParser pscParser;
    private final CompanyRepository companyRepository;
    private final SearchQueryCacheRepository searchQueryCacheRepository;
    private final ScrapingProperties props;
    private final java.util.concurrent.ConcurrentHashMap<String, Object> queryLocks = new java.util.concurrent.ConcurrentHashMap<>();

    public CompanySearchService(PoliteHttpClient httpClient,
                                CompanySearchResultsParser searchParser,
                                CompanyOverviewParser overviewParser,
                                OfficersPageParser officersParser,
                                PscPageParser pscParser,
                                CompanyRepository companyRepository,
                                SearchQueryCacheRepository searchQueryCacheRepository,
                                ScrapingProperties props) {
        this.httpClient = httpClient;
        this.searchParser = searchParser;
        this.overviewParser = overviewParser;
        this.officersParser = officersParser;
        this.pscParser = pscParser;
        this.companyRepository = companyRepository;
        this.searchQueryCacheRepository = searchQueryCacheRepository;
        this.props = props;
    }

    public List<Company> search(String rawQuery, boolean forceRefresh) {
        String normalized = normalize(rawQuery);
        List<String> companyNumbers = resolveCompanyNumbers(rawQuery, normalized, forceRefresh);

        List<Company> results = new ArrayList<>();
        for (String number : companyNumbers) {
            results.add(getOrRefreshCompany(number, forceRefresh));
        }
        return results;
    }

    private List<String> resolveCompanyNumbers(String rawQuery, String normalized, boolean forceRefresh) {
        Object lock = queryLocks.computeIfAbsent(normalized, k -> new Object());
        synchronized (lock) {
            Optional<SearchQueryCache> cached = searchQueryCacheRepository.findByNormalizedQuery(normalized);
            boolean cacheIsFresh = cached.isPresent() && !isStale(cached.get().getFetchedAt(), props.getSearchCacheTtlHours());

            if (cacheIsFresh && !forceRefresh) {
                log.info("Query cache hit for '{}'", normalized);
                return splitNumbers(cached.get().getCompanyNumbers());
            }

            log.info("Query cache miss/stale for '{}' — scraping search results", normalized);
            List<String> numbers = scrapeSearchResults(rawQuery);

            SearchQueryCache row = cached.orElseGet(SearchQueryCache::new);
            row.setNormalizedQuery(normalized);
            row.setRawQuery(rawQuery);
            row.setCompanyNumbers(String.join(",", numbers));
            row.setResultCount(numbers.size());
            row.setFetchedAt(Instant.now());
            searchQueryCacheRepository.save(row);

            return numbers;
        }
    }

    private List<String> scrapeSearchResults(String rawQuery) {
        List<String> numbers = new ArrayList<>();
        int startIndex = 0;
        int pageSize = 20;
        int maxPages = (props.getMaxCompaniesPerQuery() / pageSize) + 3; // small buffer, hard ceiling
        int pagesFetched = 0;

        while (numbers.size() < props.getMaxCompaniesPerQuery() && pagesFetched < maxPages) {
            String url = props.getBaseUrl() + "/search/companies?q="
                    + java.net.URLEncoder.encode(rawQuery, java.nio.charset.StandardCharsets.UTF_8)
                    + "&start_index=" + startIndex;
            try {
                Document doc = httpClient.fetch(url);
                Map<String, String> pageResults = searchParser.parseCompanyLinks(doc);
                pagesFetched++;

                if (pageResults.isEmpty()) {
                    break; // genuinely no more results
                }

                int sizeBefore = numbers.size();
                for (String number : pageResults.keySet()) {
                    if (numbers.size() >= props.getMaxCompaniesPerQuery()) break;
                    if (!numbers.contains(number)) {
                        numbers.add(number);
                    }
                }

                if (numbers.size() == sizeBefore) {
                    log.warn("Search page at start_index={} returned no new companies — stopping pagination", startIndex);
                    break;
                }

                startIndex += pageSize;
            } catch (Exception e) {
                log.warn("Failed to fetch search page at start_index={}: {}", startIndex, e.getMessage());
                break;
            }
        }
        return numbers;
    }

    @Transactional
    protected Company getOrRefreshCompany(String companyNumber, boolean forceRefresh) {
        Optional<Company> existing = companyRepository.findById(companyNumber);

        boolean needsRefresh = existing.isEmpty()
                || forceRefresh
                || isStale(existing.get().getLastScrapedAt(), props.getCompanyCacheTtlHours());

        if (!needsRefresh) {
            log.info("Company {} is fresh, using cached data", companyNumber);
            return existing.get();
        }

        try {
            return scrapeAndSaveCompany(companyNumber, existing.orElseGet(Company::new));
        } catch (Exception e) {
            log.warn("Failed to refresh company {}: {}. Falling back to cached data if available.",
                    companyNumber, e.getMessage());
            return existing.orElseThrow(() -> new RuntimeException(
                    "Could not fetch company " + companyNumber + " and no cached copy exists", e));
        }
    }

    private Company scrapeAndSaveCompany(String companyNumber, Company company) throws Exception {
        String overviewUrl = props.getBaseUrl() + "/company/" + companyNumber;
        Document overviewDoc = httpClient.fetch(overviewUrl);
        overviewParser.populateFromOverviewPage(company, overviewDoc, companyNumber, overviewUrl);

        String officersUrl = overviewUrl + "/officers";
        Document officersDoc = httpClient.fetch(officersUrl);
        officersParser.populateOfficers(company, officersDoc);

        try {
            String pscUrl = overviewUrl + "/persons-with-significant-control";
            Document pscDoc = httpClient.fetch(pscUrl);
            pscParser.populatePscs(company, pscDoc);
        } catch (Exception e) {
            log.warn("PSC fetch failed for {} (non-fatal, PSC is optional): {}", companyNumber, e.getMessage());
        }

        company.setLastScrapedAt(Instant.now());
        return companyRepository.save(company);
    }


    private boolean isStale(Instant timestamp, long ttlHours) {
        if (timestamp == null) return true;
        return timestamp.isBefore(Instant.now().minus(ttlHours, ChronoUnit.HOURS));
    }

    private String normalize(String query) {
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }

    private List<String> splitNumbers(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.asList(csv.split(","));
    }
}