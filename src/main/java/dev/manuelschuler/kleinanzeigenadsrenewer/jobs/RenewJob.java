package dev.manuelschuler.kleinanzeigenadsrenewer.jobs;

import dev.manuelschuler.kleinanzeigenadsrenewer.exceptions.ImapServerException;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.AdMailScraperHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.HttpClientHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.MoveMailHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.model.ImapServer;
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

    private List<ImapServer> imapServers;

    private final Logger logger = LoggerFactory.getLogger(RenewJob.class);

    public static String AD_MAIL_SUBJECT = "Deine Anzeige läuft in einer Woche aus";
    public static int READ_LAST_N_MAILS = 100;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    @SneakyThrows
    public void onRun() {
        this.logger.info("Found {} IMAP servers to check...", this.imapServers.size());
        this.imapServers.forEach(this::processImapServer);
        this.logger.info("Finished checking all IMAP servers. Next check scheduled in 1 hour.\n");
    }

    @SneakyThrows
    private void processImapServer(ImapServer imapServer) {
        try {
            imapServer.connect();

            this.logger.info("Connected to the IMAP server: {}", imapServer.getFriendlyName());

            Folder inboxFolder = imapServer.getInboxFolder();
            Message[] messages = inboxFolder.getMessages();

            int messageCount = messages.length - 1;
            int targetIndex = messageCount - READ_LAST_N_MAILS;

            this.logger.info(
                    "Found {} mail in IMAP server {} to check", messageCount, imapServer.getFriendlyName());

            List<Message> adMails =
                    Arrays.stream(messages)
                            .skip(targetIndex)
                            .filter(
                                    mail -> {
                                        try {
                                            return mail.getSubject().equals(AD_MAIL_SUBJECT);
                                        } catch (MessagingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                            .toList();

            adMails.forEach(mail -> processAdMail(mail, imapServer));

            this.logger.info(
                    "Found {} ad mails for IMAP server {}", adMails.size(), imapServer.getFriendlyName());
        } catch (ImapServerException exception) {
            this.logger.error("Failed to connect to the IMAP server: {}", imapServer.getFriendlyName());
        }
    }

    @SneakyThrows
    private void processAdMail(Message adMail, ImapServer imapServer) {
        String link = AdMailScraperHelper.parseRenewLink(adMail);
        String resultHtml = HttpClientHelper.doGetRequest(link);
        Optional<String> errorMessage = AdMailScraperHelper.getResultErrorMessage(resultHtml);

        if (errorMessage.isEmpty()) {
            MoveMailHelper.moveMail(adMail, imapServer.getInboxFolder(), imapServer.getProcessedMailsFolder());
            this.logger.info("Ad was successfully renewed.");
        } else {
            this.logger.info("Ad failed to renew with the reason: {}", errorMessage.get());
        }
    }
}
