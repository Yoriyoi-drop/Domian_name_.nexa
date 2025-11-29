package com.myproject.nexa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be rate limited
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    
    /**
     * Rate limit type - how to identify the client
     */
    enum RateLimitType {
        IP,        // Rate limit by IP address
        USER,      // Rate limit by user ID (requires authentication)
        ENDPOINT,  // Rate limit by endpoint
        CUSTOM     // Custom rate limit key
    }
    
    /**
     * The maximum number of requests allowed within the time window
     * Default: value from configuration
     */
    int limit() default -1; // Use default from config if -1
    
    /**
     * The time window in seconds
     * Default: value from configuration
     */
    long window() default -1; // Use default from config if -1
    
    /**
     * How to identify the client for rate limiting
     * Default: IP address
     */
    RateLimitType by() default RateLimitType.IP;
    
    /**
     * Custom key for rate limiting (used when by() is CUSTOM)
     */
    String customKey() default "";
    
    /**
     * Whether to apply rate limiting in development mode
     * Default: true (apply rate limiting in all environments)
     */
    boolean applyInDev() default true;
}