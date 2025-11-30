package com.myproject.nexa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling rate limiting using Redis
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Check if the request should be allowed based on rate limit
     * @param key The unique identifier for rate limiting (e.g., IP address, user ID)
     * @param limit The maximum number of requests allowed
     * @param duration The time window in seconds
     * @return true if request is allowed, false otherwise
     */
    public boolean isAllowed(String key, int limit, int duration) {
        String rateLimitKey = "rate_limit:" + key;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String currentCountStr = ops.get(rateLimitKey);
        long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : 0;

        if (currentCount >= limit) {
            // Check if we're still in the time window
            Long ttl = redisTemplate.getExpire(rateLimitKey);
            if (ttl != null && ttl > 0) {
                log.warn("Rate limit exceeded for key: {}", key);
                return false;
            } else {
                // TTL expired, reset counter
                currentCount = 0;
            }
        }

        // Increment the counter
        currentCount++;
        ops.set(rateLimitKey, String.valueOf(currentCount), Duration.ofSeconds(duration));

        log.debug("Rate limit increment for key: {}, count: {}", key, currentCount);
        return true;
    }

    /**
     * Check rate limit with IP address
     */
    public boolean isAllowedByIP(String ip, int limit, int duration) {
        return isAllowed("ip:" + ip, limit, duration);
    }

    /**
     * Check rate limit by user ID
     */
    public boolean isAllowedByUser(String userId, int limit, int duration) {
        return isAllowed("user:" + userId, limit, duration);
    }

    /**
     * Check rate limit by endpoint
     */
    public boolean isAllowedByEndpoint(String endpoint, String identifier, int limit, int duration) {
        return isAllowed("endpoint:" + endpoint + ":" + identifier, limit, duration);
    }

    /**
     * Get remaining requests for a key
     */
    public long getRemaining(String key, int limit) {
        String rateLimitKey = "rate_limit:" + key;
        String currentCountStr = redisTemplate.opsForValue().get(rateLimitKey);
        long currentCount = currentCountStr != null ? Long.parseLong(currentCountStr) : 0;
        return Math.max(0, limit - currentCount);
    }
}