package dev.manuelschuler.kleinanzeigenadsrenewer.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Optional;

public class ResultPageScrapeService extends ScrapeService {

    private static final String WARNING_BOX_CLASS_NAME = ".outcomebox-warning";
    private static final String WARNING_BOX_TEXT = ".outcomebox-warning p";

    public ResultPageScrapeService(String html) {
        super(html);
    }

    public Optional<String> getErrorMessage() {
        Document document = this.getDocument();
        Elements warningBox = document.select(WARNING_BOX_CLASS_NAME);

        if (warningBox.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(document.select(WARNING_BOX_TEXT).text());
    }

}
