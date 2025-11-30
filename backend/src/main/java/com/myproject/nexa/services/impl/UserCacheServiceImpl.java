package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";
    private static final String USERS_CACHE_PREFIX = "users:";
    private static final int USER_CACHE_TTL = 3600; // 1 hour

    @Override
    public void cacheUser(UserResponse user) {
        if (user != null && user.getId() != null) {
            String key = USER_CACHE_PREFIX + user.getId();
            redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL, TimeUnit.SECONDS);
            log.debug("Cached user with ID: {}", user.getId());
        }
    }

    @Override
    public UserResponse getCachedUser(Long userId) {
        if (userId != null) {
            String key = USER_CACHE_PREFIX + userId;
            UserResponse user = (UserResponse) redisTemplate.opsForValue().get(key);
            if (user != null) {
                log.debug("Retrieved cached user with ID: {}", userId);
            }
            return user;
        }
        return null;
    }

    @Override
    public void evictUser(Long userId) {
        if (userId != null) {
            String key = USER_CACHE_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("Evicted cached user with ID: {}", userId);
        }
    }

    @Override
    public void evictAllUsers() {
        // Delete all keys with user prefix
        redisTemplate.delete(redisTemplate.keys(USER_CACHE_PREFIX + "*"));
        log.debug("Evicted all cached users");
    }

    @Override
    public void cacheAllUsers(Page<UserResponse> users, String cacheKey) {
        if (users != null && cacheKey != null) {
            String key = USERS_CACHE_PREFIX + cacheKey;
            redisTemplate.opsForValue().set(key, users, USER_CACHE_TTL, TimeUnit.SECONDS);
            log.debug("Cached users with key: {}", cacheKey);
        }
    }

    @Override
    public Page<UserResponse> getCachedAllUsers(String cacheKey) {
        if (cacheKey != null) {
            String key = USERS_CACHE_PREFIX + cacheKey;
            Page<UserResponse> users = (Page<UserResponse>) redisTemplate.opsForValue().get(key);
            if (users != null) {
                log.debug("Retrieved cached users with key: {}", cacheKey);
            }
            return users;
        }
        return null;
    }
}