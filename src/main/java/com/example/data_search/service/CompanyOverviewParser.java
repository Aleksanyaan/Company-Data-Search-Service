package com.example.data_search.service;

import com.example.data_search.entity.Company;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class CompanyOverviewParser {

    private static final DateTimeFormatter CH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    public void populateFromOverviewPage(Company company, Document doc, String companyNumber, String sourceUrl) {
        company.setCompanyNumber(companyNumber);
        company.setSourceUrl(sourceUrl);

        var h1 = doc.select("h1.heading-xlarge").first();
        company.setCompanyName(h1 != null ? h1.text().trim() : null);

        var address = doc.select("#roa-address").first();
        company.setRegisteredOfficeAddress(address != null ? cleanText(address.text()) : null);

        var status = doc.select("#company-status").first();
        company.setStatus(status != null ? cleanText(status.text()) : null);

        var type = doc.select("#company-type-value").first();
        company.setCompanyType(type != null ? cleanText(type.text()) : null);

        var incorporated = doc.select("#company-creation-date").first();
        company.setIncorporationDate(parseDateSafely(incorporated != null ? incorporated.text() : null));

        var dissolved = doc.select("#cessation-date").first();
        company.setDissolutionDate(parseDateSafely(dissolved != null ? dissolved.text() : null));

        var sicList = doc.select("h2#sic-title").first();
        if (sicList != null) {
            var ul = sicList.nextElementSibling();
            if (ul != null) {
                String sics = String.join(", ", ul.select("li").eachText());
                company.setSicCodes(sics.isBlank() ? null : sics);
            }
        }
    }

    private String cleanText(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private LocalDate parseDateSafely(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text.trim(), CH_DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}