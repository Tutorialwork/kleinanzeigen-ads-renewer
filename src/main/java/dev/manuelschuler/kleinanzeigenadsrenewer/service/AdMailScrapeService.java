package dev.manuelschuler.kleinanzeigenadsrenewer.service;

import jakarta.mail.Message;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class AdMailScrapeService extends ScrapeService {

    private static final String RENEW_BUTTON_CLASS_NAME = ".cta-button-outline";
    private static final String AD_PAGE_URL = "s-anzeige";

    public AdMailScrapeService(Message mail) {
        super(mail);
    }

    public String getRenewLink() {
        Document document = this.getDocument();
        Elements renewButton = document.select(RENEW_BUTTON_CLASS_NAME);
        return renewButton.attr("href");
    }

    public String getAdName() {
        Document document = this.getDocument();
        List<Element> matchingElements = document.select("a").stream().filter(element -> element.attr("href").contains(AD_PAGE_URL)).toList();
        return matchingElements.getFirst().text();
    }
}
