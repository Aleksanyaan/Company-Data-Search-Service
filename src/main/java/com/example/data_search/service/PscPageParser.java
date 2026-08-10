package com.example.data_search.service;

import com.example.data_search.entity.Company;
import com.example.data_search.entity.PersonWithSignificantControl;
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
public class PscPageParser {

    private static final Logger log = LoggerFactory.getLogger(PscPageParser.class);

    private static final DateTimeFormatter CH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    public void populatePscs(Company company, Document doc) {
        company.getPersonsWithSignificantControl().clear();

        Elements blocks = doc.select("div[class^=appointment-]");
        if (blocks.isEmpty()) {
            log.info("No PSC blocks found for company {} (may genuinely have none)",
                    company.getCompanyNumber());
            return;
        }

        for (Element block : blocks) {
            try {
                PersonWithSignificantControl psc = parsePscBlock(block, company);
                if (psc != null) {
                    company.getPersonsWithSignificantControl().add(psc);
                }
            } catch (Exception e) {
                log.warn("Failed to parse a PSC block for company {}: {}",
                        company.getCompanyNumber(), e.getMessage());
            }
        }
    }

    private PersonWithSignificantControl parsePscBlock(Element block, Company company) {
        Element nameEl = block.select("h2, a").first();
        if (nameEl == null || nameEl.text().isBlank()) {
            return null;
        }
        String rawName = nameEl.text().trim();
        String cleanedName = rawName.replaceAll("\\s+(Active|Ceased|Resigned)$", "").trim();

        PersonWithSignificantControl psc = new PersonWithSignificantControl();
        psc.setCompany(company);
        psc.setName(cleanedName);

        var natureHeading = block.select("dt, p, span").stream()
                .filter(e -> e.ownText().trim().equalsIgnoreCase("Nature of control"))
                .findFirst();
        if (natureHeading.isPresent()) {
            Element listContainer = natureHeading.get().nextElementSibling();
            if (listContainer != null) {
                Elements items = listContainer.select("li");
                if (!items.isEmpty()) {
                    psc.setNatureOfControl(String.join("; ", items.eachText()));
                } else {
                    psc.setNatureOfControl(listContainer.text().trim());
                }
            }
        }

        psc.setNotifiedOn(parseDateSafely(HtmlLabelUtils.findValueByLabel(block, "Notified on")));

        return psc;
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