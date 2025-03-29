package dev.manuelschuler.kleinanzeigenadsrenewer.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Getter
public abstract class ScrapeService {

    private final Document document;

    public ScrapeService(String html) {
        this.document = Jsoup.parse(html);
    }

    public ScrapeService(Message mail) {
        try {
            String html = (String) mail.getContent();
            this.document = Jsoup.parse(html);
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Scraping failed", e);
        }
    }
}
