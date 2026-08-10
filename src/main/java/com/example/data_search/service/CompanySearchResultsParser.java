package com.example.data_search.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CompanySearchResultsParser {

    private static final Pattern COMPANY_LINK = Pattern.compile("^/company/([A-Za-z0-9]{6,10})$");

    public Map<String, String> parseCompanyLinks(Document doc) {
        Map<String, String> results = new LinkedHashMap<>();

        Elements links = doc.select("a[href^=/company/]");
        for (Element link : links) {
            String href = link.attr("href");
            String path = href.split("[?#]")[0];
            Matcher m = COMPANY_LINK.matcher(path);
            if (!m.matches()) {
                continue;
            }
            String number = m.group(1).toUpperCase();
            String name = link.text().trim();
            if (name.isEmpty()) {
                continue;
            }
            results.putIfAbsent(number, name);
        }
        return results;
    }

    public boolean hasResults(Document doc) {
        return !parseCompanyLinks(doc).isEmpty();
    }
}