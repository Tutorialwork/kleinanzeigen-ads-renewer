package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import jakarta.mail.Folder;
import jakarta.mail.Store;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderConfig {

    @Bean
    @SneakyThrows
    public Folder inboxFolder(Store store) {
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        return inbox;
    }

    @Bean
    @SneakyThrows
    public Folder processedMailsFolder(Store store) {
        Folder processedMailsFolder = store.getFolder("Kleinanzeigen Mails");
        if (!processedMailsFolder.exists()) {
            processedMailsFolder.create(Folder.HOLDS_MESSAGES);
        }

        processedMailsFolder.open(Folder.READ_WRITE);

        return processedMailsFolder;
    }

}
