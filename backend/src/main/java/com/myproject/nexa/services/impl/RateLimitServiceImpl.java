package com.myproject.nexa.services.impl;

import com.myproject.nexa.services.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private final RedisTemplate<String, String> customStringRedisTemplate;

    // Lua script for atomic rate limiting operation
    private static final String RATE_LIMIT_SCRIPT = "local key = KEYS[1]\n" +
            "local limit = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local current = redis.call('GET', key)\n" +
            "if current == false then\n" +
            "  redis.call('SET', key, 1)\n" +
            "  redis.call('EXPIRE', key, window)\n" +
            "  return {1, limit - 1, window}\n" +
            "end\n" +
            "current = tonumber(current)\n" +
            "if current < limit then\n" +
            "  local newval = redis.call('INCR', key)\n" +
            "  local ttl = redis.call('TTL', key)\n" +
            "  return {newval, limit - newval, ttl}\n" +
            "else\n" +
            "  local ttl = redis.call('TTL', key)\n" +
            "  return {current, 0, ttl}\n" +
            "end";

    @Override
    public boolean isAllowed(String key, int limit, long window, TimeUnit unit) {
        return isAllowed(key, null, limit, window, unit);
    }

    @Override
    public boolean isAllowed(String key, String identifier, int limit, long window, TimeUnit unit) {
        String finalKey = identifier != null ? key + ":" + identifier : key;

        List<Long> result = customStringRedisTemplate.execute(
                RedisScript.of(RATE_LIMIT_SCRIPT, List.class),
                Collections.singletonList(finalKey),
                String.valueOf(limit),
                String.valueOf(unit.toSeconds(window)));

        if (result != null && !result.isEmpty()) {
            Long currentRequests = result.get(0);
            Long remaining = result.get(1);

            log.debug("Rate limit check for key '{}': current={}, remaining={}, limit={}",
                    finalKey, currentRequests, remaining, limit);

            return currentRequests <= limit;
        }

        // If script execution failed, allow request to go through to not block users
        log.warn("Rate limit script execution failed for key '{}', allowing request", finalKey);
        return true;
    }

    @Override
    public long getRemainingRequests(String key, long window, TimeUnit unit) {
        String count = customStringRedisTemplate.opsForValue().get(key);
        if (count != null) {
            try {
                int current = Integer.parseInt(count);
                // We need to know the limit to calculate remaining - would need to enhance this
                // in production
                // For now, using a default of 100 requests
                return 100 - current; // This is a simplified implementation
            } catch (NumberFormatException e) {
                log.warn("Invalid count value in Redis for key '{}': {}", key, count);
                return 100;
            }
        }
        return 100; // Default if no count exists
    }

    @Override
    public long getResetTime(String key, long window, TimeUnit unit) {
        Long ttl = customStringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }

    @Override
    public boolean isIpAllowed(String ip) {
        // Use a default rate limit of 100 requests per hour for IP-based limits
        return isAllowed("ip:" + ip, "default", 100, 3600, TimeUnit.SECONDS);
    }

    @Override
    public long getRemainingRequests(String key) {
        String count = customStringRedisTemplate.opsForValue().get(key);
        if (count != null) {
            try {
                int current = Integer.parseInt(count);
                // Default to 100 requests
                return 100 - current;
            } catch (NumberFormatException e) {
                log.warn("Invalid count value in Redis for key '{}': {}", key, count);
                return 100;
            }
        }
        return 100; // Default if no count exists
    }

    @Override
    public long getResetTime(String key) {
        Long ttl = customStringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }

    @Override
    public void resetLimit(String key) {
        customStringRedisTemplate.delete(key);
        log.debug("Rate limit reset for key: {}", key);
    }
}