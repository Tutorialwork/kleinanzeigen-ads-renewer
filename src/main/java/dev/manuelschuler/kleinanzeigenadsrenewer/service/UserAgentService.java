package dev.manuelschuler.kleinanzeigenadsrenewer.service;

import dev.manuelschuler.kleinanzeigenadsrenewer.model.UserAgent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserAgentService {

    private final UserAgent[] userAgents;
    private final AtomicReference<Double> totalWeight = new AtomicReference<>((double) 0);

    public UserAgentService(UserAgent[] userAgents) {
        this.userAgents = userAgents;
        this.calculateTotalWeight();
    }

    public String getRandomUserAgent() {
        double randomValue = ThreadLocalRandom.current().nextDouble(0, totalWeight.get());

        AtomicInteger cumulativeWeight = new AtomicInteger();
        return Arrays.stream(userAgents)
                .filter(userAgent -> {
                    cumulativeWeight.addAndGet((int) Math.round(userAgent.getPct()));
                    return randomValue < cumulativeWeight.get();
                })
                .map(UserAgent::getUa)
                .findFirst()
                .orElse(null);
    }

    private void calculateTotalWeight() {
        Arrays.stream(userAgents).map(UserAgent::getPct).forEach(pct -> totalWeight.updateAndGet(currentValue -> currentValue + pct));
    }

}
