package com.example.data_search.dto;

import java.util.List;

public class SearchResponse {
    private List<String> queries;
    private int resultCount;
    private boolean forceRefresh;
    private List<CompanyDto> companies;

    public List<String> getQueries() { return queries; }
    public void setQueries(List<String> queries) { this.queries = queries; }

    public int getResultCount() { return resultCount; }
    public void setResultCount(int resultCount) { this.resultCount = resultCount; }

    public boolean isForceRefresh() { return forceRefresh; }
    public void setForceRefresh(boolean forceRefresh) { this.forceRefresh = forceRefresh; }

    public List<CompanyDto> getCompanies() { return companies; }
    public void setCompanies(List<CompanyDto> companies) { this.companies = companies; }
}