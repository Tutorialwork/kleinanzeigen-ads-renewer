package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import dev.manuelschuler.kleinanzeigenadsrenewer.model.ImapServer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "imap.server")
@Data
public class ImapServerConfig {

    private String host;
    private String username;
    private String password;

    @Bean
    public ImapServer imapServer() {
        return new ImapServer(this.host, this.username, this.password);
    }

}
