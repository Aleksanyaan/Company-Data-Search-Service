package com.example.data_search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "companies-house")
public class ScrapingProperties {

    private String baseUrl = "https://find-and-update.company-information.service.gov.uk";
    private String userAgent = "CompanySearchService/1.0 (contact: alexanyan.jenya@gmail.com)";
    private long requestDelayMs = 500;
    private int maxCompaniesPerQuery = 100;
    private int connectTimeoutMs = 10000;
    private long searchCacheTtlHours = 24;
    private long companyCacheTtlHours = 168;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public long getRequestDelayMs() { return requestDelayMs; }
    public void setRequestDelayMs(long requestDelayMs) { this.requestDelayMs = requestDelayMs; }

    public int getMaxCompaniesPerQuery() { return maxCompaniesPerQuery; }
    public void setMaxCompaniesPerQuery(int maxCompaniesPerQuery) { this.maxCompaniesPerQuery = maxCompaniesPerQuery; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public long getSearchCacheTtlHours() { return searchCacheTtlHours; }
    public void setSearchCacheTtlHours(long searchCacheTtlHours) { this.searchCacheTtlHours = searchCacheTtlHours; }

    public long getCompanyCacheTtlHours() { return companyCacheTtlHours; }
    public void setCompanyCacheTtlHours(long companyCacheTtlHours) { this.companyCacheTtlHours = companyCacheTtlHours; }
}