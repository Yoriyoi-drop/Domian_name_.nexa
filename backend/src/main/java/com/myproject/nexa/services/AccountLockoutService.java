package com.myproject.nexa.services;

import com.myproject.nexa.config.properties.AppProperties;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for handling account lockout and brute force protection
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountLockoutService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private static final String LOGIN_ATTEMPT_PREFIX = "login_attempt:";
    private static final String ACCOUNT_LOCKOUT_PREFIX = "account_lockout:";

    /**
     * Record a failed login attempt for the given username
     * @param username The username for which to record the failed attempt
     * @return true if account should be locked, false otherwise
     */
    public boolean recordFailedAttempt(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        String attemptsStr = redisTemplate.opsForValue().get(key);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        attempts++;
        redisTemplate.opsForValue().set(key, String.valueOf(attempts), 
                appProperties.getSecurity().getLockoutDurationMinutes(), TimeUnit.MINUTES);

        log.info("Failed login attempt #{} for user: {}", attempts, username);

        if (attempts >= appProperties.getSecurity().getMaxLoginAttempts()) {
            // Lock the account
            lockAccount(username);
            return true;
        }

        return false;
    }

    /**
     * Reset failed login attempts for the given username
     */
    public void resetAttempts(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        redisTemplate.delete(key);
        log.info("Reset login attempts for user: {}", username);
    }

    /**
     * Check if an account is locked
     */
    public boolean isAccountLocked(String username) {
        String lockoutKey = ACCOUNT_LOCKOUT_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockoutKey));
    }

    /**
     * Lock an account after too many failed attempts
     */
    public void lockAccount(String username) {
        String lockoutKey = ACCOUNT_LOCKOUT_PREFIX + username;
        redisTemplate.opsForValue().set(lockoutKey, "locked", 
                appProperties.getSecurity().getLockoutDurationMinutes(), TimeUnit.MINUTES);
        
        // Update user account status in database if needed
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
        
        log.warn("Account {} has been locked due to too many failed login attempts", username);
    }

    /**
     * Unlock an account
     */
    public void unlockAccount(String username) {
        String attemptsKey = LOGIN_ATTEMPT_PREFIX + username;
        String lockoutKey = ACCOUNT_LOCKOUT_PREFIX + username;
        
        redisTemplate.delete(attemptsKey);
        redisTemplate.delete(lockoutKey);
        
        // Update user account status in database
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
        }
        
        log.info("Account {} has been unlocked", username);
    }

    /**
     * Get the number of remaining attempts before lockout
     */
    public int getRemainingAttempts(String username) {
        String key = LOGIN_ATTEMPT_PREFIX + username;
        String attemptsStr = redisTemplate.opsForValue().get(key);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        
        int maxAttempts = appProperties.getSecurity().getMaxLoginAttempts();
        return Math.max(0, maxAttempts - attempts);
    }

    /**
     * Get the time remaining until account unlock in seconds
     */
    public long getUnlockTime(String username) {
        String lockoutKey = ACCOUNT_LOCKOUT_PREFIX + username;
        Long timeRemaining = redisTemplate.getExpire(lockoutKey, TimeUnit.SECONDS);
        return timeRemaining != null ? timeRemaining : 0;
    }
}