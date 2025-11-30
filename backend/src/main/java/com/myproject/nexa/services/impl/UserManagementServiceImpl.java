package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ResourceNotFoundException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.RoleRepository;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.UserCacheService;
import com.myproject.nexa.services.UserManagementService;
import com.myproject.nexa.services.UserValidationService;
import com.myproject.nexa.utils.AuditLogUtil;
import com.myproject.nexa.utils.ObservabilityUtil;
import com.myproject.nexa.utils.RequestTracingUtil;
import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObservabilityUtil observabilityUtil;
    private final AuditLogUtil auditLogUtil;
    private final SecurityUtil securityUtil;
    private final UserValidationService userValidationService;
    private final UserCacheService userCacheService;
    private final com.myproject.nexa.mapper.UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String correlationId = RequestTracingUtil.getOrCreateCorrelationId();
        log.info("Creating new user with username: {} and correlation ID: {}", request.getUsername(), correlationId);

        return observabilityUtil.timeOperation("user.create", () -> {
            userValidationService.validateUserCreation(request);

            // Check for existing username and email
            userValidationService.validateUsernameUniqueness(request.getUsername(), null);
            userValidationService.validateEmailUniqueness(request.getEmail(), null);

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setCreatedAt(LocalDateTime.now());

            // Assign default USER role
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Default USER role not found"));
            user.setRoles(List.of(userRole));

            try {
                User savedUser = userRepository.save(user);
                log.info("User created successfully with ID: {} and correlation ID: {}", savedUser.getId(),
                        correlationId);

                // Record audit log
                auditLogUtil.logUserAction(
                        savedUser.getId().toString(),
                        "USER_CREATE",
                        "User",
                        Map.of("username", savedUser.getUsername(), "email", savedUser.getEmail()));

                // Record business metric
                observabilityUtil.recordBusinessMetric("user.count", "create", 1.0, "operation", "create");

                UserResponse response = userMapper.toUserResponse(savedUser);
                // Cache the newly created user
                userCacheService.cacheUser(response);

                return response;
            } catch (DataIntegrityViolationException e) {
                log.error("Error creating user due to data integrity violation: {} | Correlation ID: {}",
                        e.getMessage(), correlationId);
                observabilityUtil.recordError("user.create", "DATA_INTEGRITY_VIOLATION");
                throw new AppException(ErrorCode.DATABASE_002, "Username or email already exists");
            }
        }, "operation", "create");
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        String correlationId = RequestTracingUtil.getOrCreateCorrelationId();
        log.info("Updating user with ID: {} using update request | Correlation ID: {}", id, correlationId);

        return observabilityUtil.timeOperation("user.update", () -> {
            userValidationService.validateUserUpdate(id, request);

            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            // Store old values for audit log
            String oldUsername = existingUser.getUsername();
            String oldEmail = existingUser.getEmail();
            String oldFirstName = existingUser.getFirstName();
            String oldLastName = existingUser.getLastName();

            // Validate uniqueness (excluding current user)
            if (request.getUsername() != null) {
                userValidationService.validateUsernameUniqueness(request.getUsername(), id);
                existingUser.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                userValidationService.validateEmailUniqueness(request.getEmail(), id);
                existingUser.setEmail(request.getEmail());
            }
            if (request.getFirstName() != null)
                existingUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null)
                existingUser.setLastName(request.getLastName());
            if (request.getPhone() != null)
                existingUser.setPhone(request.getPhone());
            if (request.getAddress() != null)
                existingUser.setAddress(request.getAddress());
            if (request.getEnabled() != null)
                existingUser.setEnabled(request.getEnabled());

            existingUser.setUpdatedAt(LocalDateTime.now());

            try {
                User updatedUser = userRepository.save(existingUser);
                log.info("User updated successfully with ID: {} | Correlation ID: {}", updatedUser.getId(),
                        correlationId);

                // Record audit log
                auditLogUtil.logUserAction(
                        updatedUser.getId().toString(),
                        "USER_UPDATE",
                        "User",
                        Map.of(
                                "oldUsername", oldUsername,
                                "newUsername", updatedUser.getUsername(),
                                "oldEmail", oldEmail,
                                "newEmail", updatedUser.getEmail()));

                // Record business metric
                observabilityUtil.recordBusinessMetric("user.count", "update", 1.0, "operation", "update");

                // Evict the cached user since it was updated
                userCacheService.evictUser(id);

                UserResponse response = userMapper.toUserResponse(updatedUser);
                // Cache the updated user
                userCacheService.cacheUser(response);

                return response;
            } catch (DataIntegrityViolationException e) {
                log.error("Error updating user due to data integrity violation: {} | Correlation ID: {}",
                        e.getMessage(), correlationId);
                observabilityUtil.recordError("user.update", "DATA_INTEGRITY_VIOLATION");
                throw new AppException(ErrorCode.DATABASE_002, "Username or email already exists");
            }
        }, "operation", "update", "userId", id.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Getting user by id: {}", id);

        // First, try to get from cache
        UserResponse cachedUser = userCacheService.getCachedUser(id);
        if (cachedUser != null) {
            log.debug("Returning cached user with ID: {}", id);
            return cachedUser;
        }

        // If not in cache, fetch from database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        UserResponse userResponse = userMapper.toUserResponse(user);

        // Cache the result
        userCacheService.cacheUser(userResponse);

        return userResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        log.debug("Getting current user: {}", username);
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Getting all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Getting users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        // Create a cache key based on page parameters
        String cacheKey = "users_page_" + pageable.getPageNumber() + "_size_" + pageable.getPageSize();

        // Try to get from cache first
        Page<UserResponse> cachedUsers = userCacheService.getCachedAllUsers(cacheKey);
        if (cachedUsers != null) {
            log.debug("Returning cached users for page: {}", pageable.getPageNumber());
            return cachedUsers;
        }

        Page<User> users = userRepository.findAll(pageable);
        Page<UserResponse> userResponses = users.map(userMapper::toUserResponse);

        // Cache the results
        userCacheService.cacheAllUsers(userResponses, cacheKey);

        return userResponses;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        // Instead of actually deleting, we'll soft delete by setting deletedAt
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        // Evict the cached user since it was deleted
        userCacheService.evictUser(id);

        log.info("User soft-deleted successfully: ID={}", id);
    }

    @Override
    @Transactional
    public UserResponse enableUser(Long id) {
        log.debug("Enabling user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User enabled successfully: ID={}", id);

        // Evict the cached user since it was updated
        userCacheService.evictUser(id);

        UserResponse response = userMapper.toUserResponse(updatedUser);
        // Cache the updated user
        userCacheService.cacheUser(response);

        return response;
    }

    @Override
    @Transactional
    public UserResponse disableUser(Long id) {
        log.debug("Disabling user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User disabled successfully: ID={}", id);

        // Evict the cached user since it was updated
        userCacheService.evictUser(id);

        UserResponse response = userMapper.toUserResponse(updatedUser);
        // Cache the updated user
        userCacheService.cacheUser(response);

        return response;
    }

    @Override
    @Transactional
    public UserResponse lockUserAccount(Long id) {
        log.debug("Locking user account with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setAccountNonLocked(false);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User account locked successfully: ID={}", id);

        // Evict the cached user since it was updated
        userCacheService.evictUser(id);

        UserResponse response = userMapper.toUserResponse(updatedUser);
        // Cache the updated user
        userCacheService.cacheUser(response);

        return response;
    }

    @Override
    @Transactional
    public UserResponse unlockUserAccount(Long id) {
        log.debug("Unlocking user account with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setAccountNonLocked(true);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User account unlocked successfully: ID={}", id);

        // Evict the cached user since it was updated
        userCacheService.evictUser(id);

        UserResponse response = userMapper.toUserResponse(updatedUser);
        // Cache the updated user
        userCacheService.cacheUser(response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(String roleName) {
        log.debug("Finding users by role: {}", roleName);
        List<User> users = userRepository.findByRoleName(roleName);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }

}