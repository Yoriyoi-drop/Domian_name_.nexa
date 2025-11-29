package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling concurrent user operations with proper locking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserConcurrencyService {

    private final UserRepository userRepository;
    private final DistributedLockService distributedLockService;
    private final UserService userService;

    /**
     * Update user with optimistic locking to prevent lost updates
     * @param id User ID
     * @param request Update request
     * @return Updated user
     */
    @Transactional
    @Retryable(value = {OptimisticLockException.class, CannotAcquireLockException.class}, maxAttempts = 3)
    public User updateUserWithOptimisticLock(Long id, UserUpdateRequest request) {
        log.debug("Updating user {} with optimistic lock", id);
        
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_001, "User not found"));
            
            // Set all updated fields
            if (request.getUsername() != null) user.setUsername(request.getUsername());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
            if (request.getAddress() != null) user.setAddress(request.getAddress());
            if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
            
            user.setUpdatedAt(LocalDateTime.now());
            
            return userRepository.save(user);
            
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock exception while updating user {}, retrying... ", id, e);
            throw e; // This will trigger a retry
        }
    }

    /**
     * Update user with distributed locking for critical operations
     * @param id User ID
     * @param request Update request
     * @return Updated user response
     */
    @Transactional
    public UserResponse updateUserWithDistributedLock(Long id, UserUpdateRequest request) {
        String lockKey = "user_update_lock:" + id;
        String lockToken = null;

        try {
            // Try to acquire distributed lock
            lockToken = distributedLockService.acquireLock(lockKey, 30); // 30 seconds timeout
            if (lockToken == null) {
                log.error("Could not acquire lock for user update: {}", id);
                throw new AppException(ErrorCode.BUSINESS_002, "User is currently being updated by another process");
            }

            // Now perform the update
            User updatedUser = updateUserWithOptimisticLock(id, request);

            // Convert to response - use the UserService to map to response
            return userService.mapToUserResponse(updatedUser);

        } finally {
            // Always release the lock
            if (lockToken != null) {
                distributedLockService.releaseLock(lockKey, lockToken);
            }
        }
    }

    /**
     * Update user balance safely with distributed lock (example of financial operation)
     * @param userId User ID
     * @param amount Amount to add/remove
     * @return Updated user
     */
    @Transactional
    public User updateUserBalance(Long userId, Double amount) {
        String lockKey = "user_balance_lock:" + userId;
        String lockToken = null;
        
        try {
            // Acquire distributed lock for balance update
            lockToken = distributedLockService.acquireLock(lockKey, 30);
            if (lockToken == null) {
                throw new AppException(ErrorCode.BUSINESS_002, "Balance is currently being updated by another process");
            }
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_001, "User not found"));
            
            // Here you would have a balance field in the User entity
            // For now, let's just update a field that might be sensitive to concurrent updates
            user.setUpdatedAt(LocalDateTime.now());
            
            return userRepository.save(user);
            
        } finally {
            // Always release the lock
            if (lockToken != null) {
                distributedLockService.releaseLock(lockKey, lockToken);
            }
        }
    }

    /**
     * Perform a critical user operation with both distributed locking and optimistic locking
     * @param userId User ID
     * @param operationDescription Description of the operation for logging
     * @param operation The operation to perform
     */
    public <T> T performCriticalOperation(Long userId, String operationDescription, java.util.function.Function<User, T> operation) {
        String lockKey = "user_critical_operation:" + userId;
        String lockToken = null;
        
        try {
            // Acquire distributed lock for critical operation
            lockToken = distributedLockService.acquireLock(lockKey, 60); // 60 seconds timeout for critical operations
            if (lockToken == null) {
                throw new AppException(ErrorCode.BUSINESS_002, 
                    "User is currently undergoing a critical operation by another process");
            }
            
            log.info("Starting critical operation for user {}: {}", userId, operationDescription);
            
            // Perform the operation with optimistic locking
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_001, "User not found"));
            
            T result = operation.apply(user);
            
            log.info("Completed critical operation for user {}: {}", userId, operationDescription);
            return result;
            
        } finally {
            // Always release the lock
            if (lockToken != null) {
                distributedLockService.releaseLock(lockKey, lockToken);
            }
        }
    }
}