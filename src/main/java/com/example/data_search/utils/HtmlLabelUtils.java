package com.example.data_search.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlLabelUtils {

    public static String findValueByLabel(Element root, String label) {
        Elements candidates = root.select("dt, dd, a, th, span, p, div");
        for (Element el : candidates) {
            String text = el.ownText().trim();
            if (text.equalsIgnoreCase(label)) {
                Element sibling = el.nextElementSibling();
                if (sibling != null && !sibling.text().trim().isEmpty()) {
                    return sibling.text().trim();
                }
                Element parent = el.parent();
                if (parent != null) {
                    Element parentSibling = parent.nextElementSibling();
                    if (parentSibling != null && !parentSibling.text().trim().isEmpty()) {
                        return parentSibling.text().trim();
                    }
                }
            }
        }
        return null;
    }

    private HtmlLabelUtils() {}
}