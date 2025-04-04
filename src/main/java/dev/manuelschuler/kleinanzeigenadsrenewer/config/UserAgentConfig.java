package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import com.google.gson.Gson;
import dev.manuelschuler.kleinanzeigenadsrenewer.model.UserAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@Data
@AllArgsConstructor
public class UserAgentConfig {

    private ResourceLoader resourceLoader;
    private Gson gson;

    @Bean
    public UserAgent[] userAgents() throws IOException {
        Resource resource = this.resourceLoader.getResource("classpath:mobile-user-agents.json");
        String json = resource.getContentAsString(StandardCharsets.UTF_8);

        return this.gson.fromJson(json, UserAgent[].class);
    }

}
