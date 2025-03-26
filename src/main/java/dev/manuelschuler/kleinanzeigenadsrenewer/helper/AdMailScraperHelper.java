package dev.manuelschuler.kleinanzeigenadsrenewer.helper;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;

public class AdMailScraperHelper {

    private static final String BUTTON_CLASS_NAME = ".cta-button-outline";
    private static final String WARNING_BOX_CLASS_NAME = ".outcomebox-warning";
    private static final String WARNING_BOX_TEXT = ".outcomebox-warning p";

    private AdMailScraperHelper() {
    }

    public static String parseRenewLink(Message adMail) {
        try {
            String content = (String) adMail.getContent();
            Document document = Jsoup.parse(content);
            Elements renewButton = document.select(BUTTON_CLASS_NAME);
            return renewButton.attr("href");
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> getResultErrorMessage(String resultHtml) {
        Document document = Jsoup.parse(resultHtml);
        Elements warningBox = document.select(WARNING_BOX_CLASS_NAME);

        if (warningBox.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(document.select(WARNING_BOX_TEXT).text());
    }

}
