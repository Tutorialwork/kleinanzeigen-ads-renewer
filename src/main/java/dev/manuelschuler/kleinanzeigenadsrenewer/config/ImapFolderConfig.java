package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "imap.folders")
@Data
public class ImapFolderConfig {

    private String processedFolderName;
    private String failedFolderName;

}
