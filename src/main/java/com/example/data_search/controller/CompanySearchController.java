package com.example.data_search.controller;

import com.example.data_search.dto.CompanyDto;
import com.example.data_search.dto.SearchResponse;
import com.example.data_search.entity.Company;
import com.example.data_search.mapper.CompanyMapper;
import com.example.data_search.service.CompanySearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CompanySearchController {

    private final CompanySearchService searchService;
    private final CompanyMapper mapper;

    public CompanySearchController(CompanySearchService searchService, CompanyMapper mapper) {
        this.searchService = searchService;
        this.mapper = mapper;
    }

    @GetMapping("/api/search")
    public SearchResponse search(
            @RequestParam("q") List<String> queries,
            @RequestParam(value = "forceRefresh", defaultValue = "false") boolean forceRefresh) {

        Map<String, Company> deduped = new LinkedHashMap<>();
        for (String query : queries) {
            List<Company> companies = searchService.search(query, forceRefresh);
            for (Company c : companies) {
                deduped.putIfAbsent(c.getCompanyNumber(), c);
            }
        }

        List<Company> combined = new ArrayList<>(deduped.values());
        List<CompanyDto> dtos = mapper.toDtos(combined);

        SearchResponse response = new SearchResponse();
        response.setQueries(queries);
        response.setForceRefresh(forceRefresh);
        response.setResultCount(dtos.size());
        response.setCompanies(dtos);
        return response;
    }
}