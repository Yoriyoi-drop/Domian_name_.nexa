package com.myproject.nexa.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Distributed locking service for handling concurrent operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {

    private final RedisTemplate<String, String> redisTemplate;
    
    // Lua script for releasing lock safely (only if lock is owned by current instance)
    private static final String RELEASE_LOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('del', KEYS[1]) " +
        "else return 0 end";

    /**
     * Acquire a distributed lock
     * @param lockKey The key for the lock
     * @param timeoutSeconds How long to hold the lock
     * @return Lock token if acquired, null if not
     */
    public String acquireLock(String lockKey, long timeoutSeconds) {
        String lockToken = UUID.randomUUID().toString();
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockToken, timeoutSeconds, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(acquired)) {
            log.debug("Lock acquired: {} with token: {}", lockKey, lockToken);
            return lockToken;
        } else {
            log.debug("Failed to acquire lock: {}", lockKey);
            return null;
        }
    }

    /**
     * Release a distributed lock
     * @param lockKey The key for the lock
     * @param lockToken The token that was returned when acquiring the lock
     * @return true if lock was released, false otherwise
     */
    public boolean releaseLock(String lockKey, String lockToken) {
        RedisScript<Long> script = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), lockToken);
        
        boolean released = (result != null && result == 1);
        if (released) {
            log.debug("Lock released: {} with token: {}", lockKey, lockToken);
        } else {
            log.debug("Failed to release lock: {} with token: {}", lockKey, lockToken);
        }
        
        return released;
    }

    /**
     * Execute a task with distributed locking
     * @param lockKey The key for the lock
     * @param timeoutSeconds How long to hold the lock
     * @param task The task to execute
     */
    public <T> T executeWithLock(String lockKey, long timeoutSeconds, java.util.concurrent.Callable<T> task) 
            throws Exception {
        String lockToken = acquireLock(lockKey, timeoutSeconds);
        if (lockToken == null) {
            throw new IllegalStateException("Could not acquire lock for: " + lockKey);
        }

        try {
            return task.call();
        } finally {
            releaseLock(lockKey, lockToken);
        }
    }

    /**
     * Check if a lock exists
     * @param lockKey The key for the lock
     * @return true if lock exists, false otherwise
     */
    public boolean isLocked(String lockKey) {
        return redisTemplate.hasKey(lockKey);
    }

    /**
     * Get the lock token if it exists
     * @param lockKey The key for the lock
     * @return The lock token or null if not locked
     */
    public String getLockToken(String lockKey) {
        return redisTemplate.opsForValue().get(lockKey);
    }
}