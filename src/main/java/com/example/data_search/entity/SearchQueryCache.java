package com.example.data_search.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "search_query_cache")
public class SearchQueryCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "normalized_query", nullable = false, unique = true)
    private String normalizedQuery;

    @Column(name = "raw_query", nullable = false)
    private String rawQuery;

    @Column(name = "company_numbers", length = 4000)
    private String companyNumbers;

    @Column(name = "result_count")
    private Integer resultCount;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;

    public SearchQueryCache() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNormalizedQuery() { return normalizedQuery; }
    public void setNormalizedQuery(String normalizedQuery) { this.normalizedQuery = normalizedQuery; }

    public String getRawQuery() { return rawQuery; }
    public void setRawQuery(String rawQuery) { this.rawQuery = rawQuery; }

    public String getCompanyNumbers() { return companyNumbers; }
    public void setCompanyNumbers(String companyNumbers) { this.companyNumbers = companyNumbers; }

    public Integer getResultCount() { return resultCount; }
    public void setResultCount(Integer resultCount) { this.resultCount = resultCount; }

    public Instant getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(Instant fetchedAt) { this.fetchedAt = fetchedAt; }
}