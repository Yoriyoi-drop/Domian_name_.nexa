package com.myproject.nexa.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Application configuration properties with validation
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank(message = "Application name is required")
    private String name = "MyProject.nexa";

    @NotBlank(message = "Application version is required")
    private String version = "1.0.0";

    @NotBlank(message = "Application description is required")
    private String description = "Enterprise Fullstack Application";

    private Security security = new Security();
    private Jwt jwt = new Jwt();
    private Database database = new Database();
    private Cache cache = new Cache();
    private RateLimit rateLimit = new RateLimit();
    private Audit audit = new Audit();

    @Data
    public static class Security {
        private boolean enableCsrf = true;
        private boolean enableHsts = true;
        private boolean enableCsp = true;
        private String passwordPolicy = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"; // At least 8 chars, 1 letter, 1 number
        private int maxLoginAttempts = 5;
        private long lockoutDurationMinutes = 30; // 30 minutes
        private String[] allowedOrigins = {"https://myproject.nexa", "https://api.myproject.nexa"};
    }

    @Data
    public static class Jwt {
        @NotBlank(message = "JWT secret is required")
        private String secret;

        @Positive(message = "JWT expiration must be positive")
        private long expiration = 86400000; // 24 hours in milliseconds

        @Positive(message = "JWT refresh token expiration must be positive")
        private long refreshTokenExpiration = 604800000; // 7 days in milliseconds

        @NotBlank(message = "JWT header is required")
        private String header = "Authorization";

        @NotBlank(message = "JWT prefix is required")
        private String prefix = "Bearer ";
    }

    @Data
    public static class Database {
        @Positive(message = "Connection pool size must be positive")
        private int maxPoolSize = 20;

        @Positive(message = "Connection timeout must be positive")
        private int connectionTimeout = 30000; // 30 seconds

        @Positive(message = "Idle timeout must be positive")
        private int idleTimeout = 600000; // 10 minutes

        @Positive(message = "Max lifetime must be positive")
        private int maxLifetime = 1800000; // 30 minutes

        private boolean enableConnectionPoolMetrics = true;
    }

    @Data
    public static class Cache {
        @Positive(message = "Cache TTL must be positive")
        private long ttlSeconds = 3600; // 1 hour

        @Positive(message = "Cache max size must be positive")
        private int maxSize = 1000;

        private boolean enabled = true;
        private String defaultCacheName = "default";
    }

    @Data
    public static class RateLimit {
        @Positive(message = "Rate limit window must be positive")
        private int windowSeconds = 60; // 1 minute

        @Positive(message = "Rate limit count must be positive")
        private int maxRequests = 100;

        private boolean enabled = true;
        private boolean enablePerUserLimit = false;
    }

    @Data
    public static class Audit {
        private boolean enabled = true;
        private boolean logSensitiveData = false;
        private String logFormat = "JSON";
        private boolean enableUserTracking = true;
    }
}