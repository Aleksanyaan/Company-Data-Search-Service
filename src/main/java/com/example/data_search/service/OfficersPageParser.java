package com.example.data_search.service;

import com.example.data_search.entity.Company;
import com.example.data_search.entity.Officer;
import com.example.data_search.utils.HtmlLabelUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class OfficersPageParser {

    private static final Logger log = LoggerFactory.getLogger(OfficersPageParser.class);

    private static final DateTimeFormatter CH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    public void populateOfficers(Company company, Document doc) {
        company.getOfficers().clear();

        Elements blocks = doc.select("div[class^=appointment-]");
        if (blocks.isEmpty()) {
            log.warn("No officer blocks found for company {} — page structure may have changed",
                    company.getCompanyNumber());
            return;
        }

        for (Element block : blocks) {
            try {
                Officer officer = parseOfficerBlock(block, company);
                if (officer != null) {
                    company.getOfficers().add(officer);
                }
            } catch (Exception e) {
                log.warn("Failed to parse an officer block for company {}: {}",
                        company.getCompanyNumber(), e.getMessage());
            }
        }
    }

    private Officer parseOfficerBlock(Element block, Company company) {
        Element nameEl = block.select("h2, a").first();
        if (nameEl == null || nameEl.text().isBlank()) {
            return null;
        }

        Officer officer = new Officer();
        officer.setCompany(company);
        officer.setName(nameEl.text().trim());

        officer.setRole(HtmlLabelUtils.findValueByLabel(block, "Role"));
        officer.setNationality(HtmlLabelUtils.findValueByLabel(block, "Nationality"));
        officer.setOccupation(HtmlLabelUtils.findValueByLabel(block, "Occupation"));
        officer.setAddress(HtmlLabelUtils.findValueByLabel(block, "Correspondence address"));

        officer.setAppointedOn(parseDateSafely(HtmlLabelUtils.findValueByLabel(block, "Appointed on")));
        officer.setResignedOn(parseDateSafely(HtmlLabelUtils.findValueByLabel(block, "Resigned on")));

        return officer;
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