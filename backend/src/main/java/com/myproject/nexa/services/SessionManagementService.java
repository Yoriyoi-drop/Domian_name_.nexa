package com.myproject.nexa.services;

import com.myproject.nexa.entities.User;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling session management including multiple device login and forced logout
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final String ACTIVE_TOKEN_PREFIX = "active_token:";

    /**
     * Register a new session for a user
     * @param userId The user ID
     * @param deviceId The device ID
     * @param token The JWT token
     */
    public void registerSession(Long userId, String deviceId, String token) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        
        // Store the session info in Redis with expiration matching the token
        String sessionInfo = deviceId + ":" + token;
        redisTemplate.opsForValue().set(
            userSessionsKey + ":" + deviceId, 
            sessionInfo, 
            tokenProvider.getExpiration(token), 
            TimeUnit.MILLISECONDS
        );
        
        // Add device to user's session list
        redisTemplate.opsForList().rightPush(userSessionsKey, deviceId);
        
        log.info("Session registered for user {} on device {}", userId, deviceId);
    }

    /**
     * Get all active sessions for a user
     */
    public List<String> getActiveSessions(Long userId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        return redisTemplate.opsForList().range(userSessionsKey, 0, -1);
    }

    /**
     * Check if a token is still valid (active in Redis)
     */
    public boolean isTokenActive(String token) {
        String tokenKey = ACTIVE_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    /**
     * Invalidate a specific session
     */
    public void invalidateSession(Long userId, String deviceId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        String sessionKey = userSessionsKey + ":" + deviceId;
        
        // Get the token associated with this session
        String sessionInfo = redisTemplate.opsForValue().get(sessionKey);
        if (sessionInfo != null) {
            String[] parts = sessionInfo.split(":", 2);
            if (parts.length == 2) {
                String token = parts[1];
                String tokenKey = ACTIVE_TOKEN_PREFIX + token;
                redisTemplate.delete(tokenKey);
            }
        }
        
        // Remove the session
        redisTemplate.delete(sessionKey);
        redisTemplate.opsForList().remove(userSessionsKey, 1, deviceId);
        
        log.info("Session invalidated for user {} on device {}", userId, deviceId);
    }

    /**
     * Force logout from all devices for a user
     */
    public void forceLogoutAllDevices(Long userId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        List<String> deviceIds = redisTemplate.opsForList().range(userSessionsKey, 0, -1);
        
        if (deviceIds != null) {
            for (String deviceId : deviceIds) {
                String sessionKey = userSessionsKey + ":" + deviceId;
                String sessionInfo = redisTemplate.opsForValue().get(sessionKey);
                
                if (sessionInfo != null) {
                    String[] parts = sessionInfo.split(":", 2);
                    if (parts.length == 2) {
                        String token = parts[1];
                        String tokenKey = ACTIVE_TOKEN_PREFIX + token;
                        redisTemplate.delete(tokenKey);
                    }
                }
                
                redisTemplate.delete(sessionKey);
            }
        }
        
        // Clear the user's session list
        redisTemplate.delete(userSessionsKey);
        
        log.info("Forced logout from all devices for user {}", userId);
    }

    /**
     * Force logout from all devices except current session
     */
    public void forceLogoutOtherDevices(Long userId, String currentDeviceId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        List<String> deviceIds = redisTemplate.opsForList().range(userSessionsKey, 0, -1);
        
        if (deviceIds != null) {
            for (String deviceId : deviceIds) {
                if (!deviceId.equals(currentDeviceId)) {
                    invalidateSession(userId, deviceId);
                }
            }
        }
        
        log.info("Forced logout from other devices for user {}", userId);
    }

    /**
     * Check if user has reached maximum concurrent sessions
     */
    public boolean hasReachedMaxSessions(Long userId) {
        List<String> activeSessions = getActiveSessions(userId);
        // For now, we'll assume a max of 5 concurrent sessions
        return activeSessions != null && activeSessions.size() >= 5;
    }

    /**
     * Cleanup expired sessions
     */
    public void cleanupExpiredSessions() {
        // This would typically be called by a scheduled job
        // For now, we're relying on Redis TTL to handle expiration
        log.debug("Session cleanup called");
    }
}