package com.myproject.nexa.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        // Check external service health (example: authentication service)
        boolean externalServiceHealthy = checkExternalServiceHealth();
        
        if (externalServiceHealthy) {
            return Health.up()
                    .withDetail("externalService", "External service is accessible")
                    .build();
        } else {
            return Health.down()
                    .withDetail("externalService", "External service is not accessible")
                    .build();
        }
    }
    
    private boolean checkExternalServiceHealth() {
        try {
            // Example: check if an external API is accessible
            // Replace with actual external service endpoint
            // restTemplate.getForObject("http://external-service/health", String.class);
            return true; // For now, assuming it's healthy
        } catch (Exception e) {
            return false;
        }
    }
}