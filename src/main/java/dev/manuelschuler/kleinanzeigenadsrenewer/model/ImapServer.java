package dev.manuelschuler.kleinanzeigenadsrenewer.model;

import dev.manuelschuler.kleinanzeigenadsrenewer.exceptions.ImapServerException;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Properties;

@AllArgsConstructor
@Data
public class ImapServer {

    private String friendlyName;
    private String host;
    private String username;
    private String password;
    private Store store;

    public void connect() throws ImapServerException {
        try {
            Properties props = new Properties();
            props.setProperty("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(this.host, this.username, this.password);

            this.store = store;
        } catch (MessagingException messagingException) {
            throw new ImapServerException();
        }
    }

    @SneakyThrows
    public Folder getInboxFolder() {
        Folder inbox = this.store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        return inbox;
    }

    @SneakyThrows
    public Folder getFolder(String folderName) {
        Folder folder = this.store.getFolder(folderName);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES);
        }

        folder.open(Folder.READ_WRITE);

        return folder;
    }
}
