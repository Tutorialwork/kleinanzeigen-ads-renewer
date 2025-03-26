package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import dev.manuelschuler.kleinanzeigenadsrenewer.model.ImapServer;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class StoreConfig {

    @Bean
    @SneakyThrows
    public Store store(ImapServer imapServer) {
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect(imapServer.getHost(), imapServer.getUsername(), imapServer.getPassword());

        return store;
    }

}
