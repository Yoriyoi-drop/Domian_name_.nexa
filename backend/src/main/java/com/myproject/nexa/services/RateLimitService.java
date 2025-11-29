package com.myproject.nexa.services;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for handling rate limiting using Redis
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AppProperties appProperties;

    /**
     * Check if the request is within rate limits
     * @param key The rate limit key (e.g., IP address, user ID)
     * @param limit The maximum number of requests allowed
     * @param windowSeconds The time window in seconds
     * @return true if within limits, false if exceeded
     */
    public boolean isAllowed(String key, int limit, long windowSeconds) {
        if (!appProperties.getRateLimit().isEnabled()) {
            return true; // Rate limiting disabled
        }

        String redisKey = "rate_limit:" + key;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount == 1) {
            // First request, set expiration
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        if (currentCount > limit) {
            log.warn("Rate limit exceeded for key: {}", key);
            return false;
        }

        return true;
    }

    /**
     * Check if the request is within rate limits, using default configuration
     * @param key The rate limit key (e.g., IP address, user ID)
     * @return true if within limits, false if exceeded
     */
    public boolean isAllowed(String key) {
        return isAllowed(
            key, 
            appProperties.getRateLimit().getMaxRequests(), 
            appProperties.getRateLimit().getWindowSeconds()
        );
    }

    /**
     * Get remaining requests for the given key
     * @param key The rate limit key
     * @return Number of remaining requests
     */
    public long getRemainingRequests(String key) {
        String redisKey = "rate_limit:" + key;
        String countStr = redisTemplate.opsForValue().get(redisKey);
        
        if (countStr == null) {
            return appProperties.getRateLimit().getMaxRequests();
        }
        
        long currentCount = Long.parseLong(countStr);
        long remaining = appProperties.getRateLimit().getMaxRequests() - currentCount;
        return Math.max(0, remaining);
    }

    /**
     * Get reset time for the given key
     * @param key The rate limit key
     * @return Time in seconds when the rate limit resets
     */
    public long getResetTime(String key) {
        String redisKey = "rate_limit:" + key;
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }

    /**
     * Check authentication-based rate limiting
     * @param userId The user ID
     * @return true if within limits, false if exceeded
     */
    public boolean isUserAllowed(Long userId) {
        if (!appProperties.getRateLimit().isEnablePerUserLimit()) {
            // Use default IP-based rate limiting
            return true;
        }
        
        String key = "user:" + userId;
        return isAllowed(
            key,
            appProperties.getRateLimit().getMaxRequests(), 
            appProperties.getRateLimit().getWindowSeconds()
        );
    }

    /**
     * Check anonymous rate limiting (e.g., by IP)
     * @param ip The IP address
     * @return true if within limits, false if exceeded
     */
    public boolean isIpAllowed(String ip) {
        String key = "ip:" + ip;
        return isAllowed(key);
    }
}