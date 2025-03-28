package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import dev.manuelschuler.kleinanzeigenadsrenewer.model.ImapServer;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "imap")
@Data
public class ImapServerConfig {

  private Map<String, ImapServer> servers;

  @Bean
  public List<ImapServer> imapServers() {
    this.servers
        .keySet()
        .forEach(serverName -> this.servers.get(serverName).setFriendlyName(serverName));
    return this.servers.values().stream().toList();
  }
}
