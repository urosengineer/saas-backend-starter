package com.urke.saasbackendstarter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HealthIndicator for monitoring the availability of an external demo API.
 */
@Component("demoApi")
public class DemoApiHealthIndicator implements HealthIndicator {

    private static final String DEMO_API_URL = "https://jsonplaceholder.typicode.com/todos/1";

    private final RestTemplate restTemplate;

    public DemoApiHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            String response = restTemplate.getForObject(DEMO_API_URL, String.class);
            if (response != null && !response.isEmpty()) {
                return Health.up()
                        .withDetail("demoApi", "Available")
                        .build();
            } else {
                return Health.down()
                        .withDetail("demoApi", "No data")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("demoApi", "Unavailable")
                    .build();
        }
    }
}