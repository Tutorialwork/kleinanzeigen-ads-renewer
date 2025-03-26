package dev.manuelschuler.kleinanzeigenadsrenewer.jobs;

import dev.manuelschuler.kleinanzeigenadsrenewer.helper.AdMailScraperHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.HttpClientHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.MoveMailHelper;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class RenewJob {

    private Folder inboxFolder;
    private Folder processedMailsFolder;

    private final Logger logger = LoggerFactory.getLogger(RenewJob.class);

    public static String AD_MAIL_SUBJECT = "Deine Anzeige läuft in einer Woche aus";
    public static int READ_LAST_N_MAILS = 100;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @SneakyThrows
    public void onRun() {
        Message[] messages = this.inboxFolder.getMessages();

        int messageCount = messages.length - 1;
        int targetIndex = messageCount - READ_LAST_N_MAILS;

        this.logger.info("Start checking {} mails for ad mails to renew", messageCount);

        List<Message> adMails = Arrays.stream(messages)
                .skip(targetIndex)
                .filter(mail -> {
                    try {
                        return mail.getSubject().equals(AD_MAIL_SUBJECT);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        adMails.forEach(this::processAdMail);

        this.logger.info("Finished checking mails for ad mails. Found {} ad mails", adMails.size());
    }

    @SneakyThrows
    private void processAdMail(Message adMail) {
        String link = AdMailScraperHelper.parseRenewLink(adMail);
        String resultHtml = HttpClientHelper.doGetRequest(link);
        Optional<String> errorMessage = AdMailScraperHelper.getResultErrorMessage(resultHtml);

        if (errorMessage.isEmpty()) {
            MoveMailHelper.moveMail(adMail, this.inboxFolder, this.processedMailsFolder);
            this.logger.info("Ad was successfully renewed.");
        } else {
            this.logger.info("Ad failed to renew with the reason: {}", errorMessage.get());
        }
    }

}
