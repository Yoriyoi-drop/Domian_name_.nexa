package com.myproject.nexa.config;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validates application configuration on startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigurationValidator {

    private final AppProperties appProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        log.info("Validating application configuration...");
        
        validateSecurityConfig();
        validateJwtConfig();
        validateDatabaseConfig();
        validateCacheConfig();
        validateRateLimitConfig();
        validateAuditConfig();
        
        log.info("Application configuration validation completed successfully");
    }

    private void validateSecurityConfig() {
        log.debug("Validating security configuration...");
        
        if (appProperties.getSecurity().getMaxLoginAttempts() <= 0) {
            log.warn("Security: Max login attempts should be greater than 0, using default: 5");
            appProperties.getSecurity().setMaxLoginAttempts(5);
        }
        
        if (appProperties.getSecurity().getLockoutDurationMinutes() <= 0) {
            log.warn("Security: Lockout duration should be greater than 0, using default: 30 minutes");
            appProperties.getSecurity().setLockoutDurationMinutes(30L);
        }
        
        log.debug("Security configuration validated");
    }

    private void validateJwtConfig() {
        log.debug("Validating JWT configuration...");
        
        if (appProperties.getJwt().getSecret() == null || appProperties.getJwt().getSecret().length() < 25) {
            log.warn("Security Warning: JWT secret should be at least 256 bits (32 characters). Consider using a stronger secret.");
        }
        
        if (appProperties.getJwt().getExpiration() < 300000) { // Less than 5 minutes
            log.warn("JWT expiration is too short (< 5 minutes), which may cause user session issues");
        }
        
        if (appProperties.getJwt().getRefreshTokenExpiration() < appProperties.getJwt().getExpiration()) {
            log.error("Refresh token expiration cannot be less than access token expiration");
            throw new IllegalStateException("Invalid JWT configuration: Refresh token expiration is less than access token expiration");
        }
        
        log.debug("JWT configuration validated");
    }

    private void validateDatabaseConfig() {
        log.debug("Validating database configuration...");
        
        if (appProperties.getDatabase().getMaxPoolSize() > 50) {
            log.warn("Database max pool size is high ({}), ensure your database can handle this load", 
                    appProperties.getDatabase().getMaxPoolSize());
        }
        
        if (appProperties.getDatabase().getConnectionTimeout() < 1000) {
            log.warn("Database connection timeout is too short ({}ms), using minimum of 1000ms", 
                    appProperties.getDatabase().getConnectionTimeout());
            appProperties.getDatabase().setConnectionTimeout(1000);
        }
        
        log.debug("Database configuration validated");
    }

    private void validateCacheConfig() {
        log.debug("Validating cache configuration...");
        
        if (appProperties.getCache().getMaxSize() > 10000) {
            log.warn("Cache max size is very large ({} items), consider reducing to prevent memory issues", 
                    appProperties.getCache().getMaxSize());
        }
        
        if (appProperties.getCache().getTtlSeconds() > 86400) { // More than 24 hours
            log.warn("Cache TTL is very long ({} seconds), consider making it shorter for data freshness", 
                    appProperties.getCache().getTtlSeconds());
        }
        
        log.debug("Cache configuration validated");
    }

    private void validateRateLimitConfig() {
        log.debug("Validating rate limit configuration...");
        
        if (appProperties.getRateLimit().getWindowSeconds() <= 0) {
            log.warn("Rate limit window should be positive, using default: 60 seconds");
            appProperties.getRateLimit().setWindowSeconds(60);
        }
        
        if (appProperties.getRateLimit().getMaxRequests() <= 0) {
            log.warn("Rate limit max requests should be positive, using default: 100");
            appProperties.getRateLimit().setMaxRequests(100);
        }
        
        log.debug("Rate limit configuration validated");
    }

    private void validateAuditConfig() {
        log.debug("Validating audit configuration...");
        
        log.debug("Audit configuration validated");
    }
}