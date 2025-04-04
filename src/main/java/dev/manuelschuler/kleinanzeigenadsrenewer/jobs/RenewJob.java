package dev.manuelschuler.kleinanzeigenadsrenewer.jobs;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.RetryPolicy;
import dev.manuelschuler.kleinanzeigenadsrenewer.config.ImapFolderConfig;
import dev.manuelschuler.kleinanzeigenadsrenewer.exceptions.ImapServerException;
import dev.manuelschuler.kleinanzeigenadsrenewer.exceptions.RenewException;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.HttpClientHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.helper.MoveMailHelper;
import dev.manuelschuler.kleinanzeigenadsrenewer.model.ImapServer;
import dev.manuelschuler.kleinanzeigenadsrenewer.service.AdMailScrapeService;
import dev.manuelschuler.kleinanzeigenadsrenewer.service.ResultPageScrapeService;
import dev.manuelschuler.kleinanzeigenadsrenewer.service.UserAgentService;
import jakarta.mail.FetchProfile;
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
    private ImapFolderConfig imapFolderConfig;
    private RetryPolicy<Object> retryPolicy;
    private UserAgentService userAgentService;

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

            Message[] lastMails = Arrays.stream(messages)
                    .skip(targetIndex)
                    .toArray(Message[]::new);
            inboxFolder.fetch(lastMails, new FetchProfile() {{
                add(FetchProfile.Item.ENVELOPE);
            }});

            List<Message> adMails =
                    Arrays.stream(lastMails)
                            .filter(
                                    mail -> {
                                        try {
                                            return mail.getSubject().equals(AD_MAIL_SUBJECT);
                                        } catch (MessagingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                            .toList();

            adMails.parallelStream().forEach(mail -> {
                try {
                    Failsafe.with(this.retryPolicy).run(() -> this.processAdMail(mail, imapServer));
                } catch (FailsafeException exception) {
                    Throwable cause = exception.getCause();
                    this.logger.error("Failed to renew ad because of the following reason: {}", cause.getMessage());
                    String failedFolderName = this.imapFolderConfig.getFailedFolderName();
                    MoveMailHelper.moveMail(mail, imapServer.getInboxFolder(), imapServer.getFolder(failedFolderName));
                }
            });

            this.logger.info(
                    "Found {} ad mails out of {} mails for IMAP server {}", adMails.size(), messageCount, imapServer.getFriendlyName());
        } catch (ImapServerException exception) {
            this.logger.error("Failed to connect to the IMAP server: {}", imapServer.getFriendlyName());
        }
    }

    @SneakyThrows
    private void processAdMail(Message adMail, ImapServer imapServer) {
        String userAgent = this.userAgentService.getRandomUserAgent();
        AdMailScrapeService mailScraper = new AdMailScrapeService(adMail);
        String link = mailScraper.getRenewLink();
        String resultHtml = HttpClientHelper.doGetRequest(link, userAgent);

        ResultPageScrapeService resultPageScraper = new ResultPageScrapeService(resultHtml);
        Optional<String> errorMessage = resultPageScraper.getErrorMessage();

        if (errorMessage.isEmpty()) {
            String processedFolderName = this.imapFolderConfig.getProcessedFolderName();
            MoveMailHelper.moveMail(adMail, imapServer.getInboxFolder(), imapServer.getFolder(processedFolderName));
            this.logger.info("Ad {} was successfully renewed.", mailScraper.getAdName());
        } else {
            throw new RenewException(errorMessage.get());
        }
    }
}
