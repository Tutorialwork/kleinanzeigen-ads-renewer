package dev.manuelschuler.kleinanzeigenadsrenewer.config;

import dev.failsafe.RetryPolicy;
import dev.manuelschuler.kleinanzeigenadsrenewer.exceptions.RenewException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryPolicyConfig {

    public static final int MAX_RETRIES = 3;
    public static final int DELAY_IN_MILLIS = 500;
    
    @Bean
    public RetryPolicy<Object> retryPolicy() {
        return RetryPolicy.builder()
                .handle(RenewException.class)
                .withDelay(Duration.ofMillis(DELAY_IN_MILLIS))
                .withMaxRetries(MAX_RETRIES)
                .build();
    }

}
