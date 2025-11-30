package com.myproject.nexa.config;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Configuration class for rate limiting settings
 * Used primarily for configuration validation through ConfigurationValidator
 */
@Component
@RequiredArgsConstructor
public class RateLimitConfig {

    private final AppProperties appProperties;
}