package com.example.data_search.service;

import com.example.data_search.config.ScrapingProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class PoliteHttpClient {

    private static final Logger log = LoggerFactory.getLogger(PoliteHttpClient.class);

    private final ScrapingProperties props;
    private volatile Instant lastRequestAt = Instant.EPOCH;

    public PoliteHttpClient(ScrapingProperties props) {
        this.props = props;
    }

    public synchronized Document fetch(String url) throws IOException {
        waitForPoliteness();
        log.info("Fetching {}", url);
        try {
            return Jsoup.connect(url)
                    .userAgent(props.getUserAgent())
                    .timeout(props.getConnectTimeoutMs())
                    .get();
        } finally {
            lastRequestAt = Instant.now();
        }
    }

    private void waitForPoliteness() {
        long elapsedMs = Instant.now().toEpochMilli() - lastRequestAt.toEpochMilli();
        long remaining = props.getRequestDelayMs() - elapsedMs;
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}