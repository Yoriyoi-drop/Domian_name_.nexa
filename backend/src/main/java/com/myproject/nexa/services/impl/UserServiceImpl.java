package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.message.UserMessageDTO;
import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.BadRequestException;
import com.myproject.nexa.exceptions.ResourceNotFoundException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.RoleRepository;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.MessageQueueService;
import com.myproject.nexa.services.UserService;
import com.myproject.nexa.utils.AuditLogUtil;
import com.myproject.nexa.utils.ObservabilityUtil;
import com.myproject.nexa.utils.RequestTracingUtil;
import com.myproject.nexa.utils.SecurityUtil;
import com.myproject.nexa.dto.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserResponse, UserRepository> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObservabilityUtil observabilityUtil;
    private final AuditLogUtil auditLogUtil;
    private final SecurityUtil securityUtil;
    private final MessageQueueService messageQueueService;

    private final com.myproject.nexa.mapper.UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          ObservabilityUtil observabilityUtil,
                          AuditLogUtil auditLogUtil,
                          SecurityUtil securityUtil,
                          MessageQueueService messageQueueService,
                          com.myproject.nexa.mapper.UserMapper userMapper) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.observabilityUtil = observabilityUtil;
        this.auditLogUtil = auditLogUtil;
        this.securityUtil = securityUtil;
        this.messageQueueService = messageQueueService;
        this.userMapper = userMapper;
    }

    // Get all users without pagination (for admin panel)
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("Retrieving all users");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        log.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id);
    }

    // This method is for the interface - returns UserResponse
    @Override
    public UserResponse findById(Long id) {
        log.info("Retrieving user response with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#user.id")
    public User update(User user) {
        log.info("Updating user with ID: {}", user.getId());

        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + user.getId());
        }

        User existingUser = existingUserOpt.get();

        // Update fields
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setEnabled(user.getEnabled());
        existingUser.setAccountNonExpired(user.getAccountNonExpired());
        existingUser.setAccountNonLocked(user.getAccountNonLocked());
        existingUser.setCredentialsNonExpired(user.getCredentialsNonExpired());

        User updatedUser = userRepository.save(existingUser);

        // Log the update
        auditLogUtil.logUserUpdate(securityUtil.getCurrentUsername().orElse("system"),
                                 updatedUser.getId(), updatedUser.getUsername());

        // Send message to queue
        messageQueueService.sendUserMessage(
            UserMessageDTO.builder()
                .userId(updatedUser.getId())
                .action("USER_UPDATED")
                .timestamp(LocalDateTime.now())
                .build()
        );

        log.info("User updated successfully: {}", updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user with ID: {}", id);

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User user = userOpt.get();

        // Soft delete by setting deletedAt
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        // Log the deletion
        auditLogUtil.logUserDeletion(securityUtil.getCurrentUsername().orElse("system"),
                                   user.getId(), user.getUsername());

        log.info("User soft deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User user = userOpt.get();
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        userRepository.save(user);

        // Log the activation
        auditLogUtil.logUserActivation(securityUtil.getCurrentUsername().orElse("system"),
                                     user.getId(), user.getUsername());

        log.info("User activated successfully: {}", id);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User user = userOpt.get();
        user.setEnabled(false);

        userRepository.save(user);

        // Log the deactivation
        auditLogUtil.logUserDeactivation(securityUtil.getCurrentUsername().orElse("system"),
                                       user.getId(), user.getUsername());

        log.info("User deactivated successfully: {}", id);
    }

    // Override the inherited abstract methods from BaseServiceImpl

    @Override
    protected UserResponse mapToResponse(User entity) {
        return userMapper.toUserResponse(entity);
    }


    @Override
    protected void updateEntity(User existing, User updated) {
        // Only update allowed fields
        if (updated.getUsername() != null)
            existing.setUsername(updated.getUsername());
        if (updated.getEmail() != null)
            existing.setEmail(updated.getEmail());
        if (updated.getFirstName() != null)
            existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null)
            existing.setLastName(updated.getLastName());
        if (updated.getPhone() != null)
            existing.setPhone(updated.getPhone());
        if (updated.getAddress() != null)
            existing.setAddress(updated.getAddress());
        if (updated.getEnabled() != null)
            existing.setEnabled(updated.getEnabled());

        existing.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Getting user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Cacheable(value = "users", key = "#username")
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
    @Cacheable(value = "usersPage", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Getting users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, User user) {
        log.debug("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update user details
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setEnabled(user.getEnabled());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String correlationId = RequestTracingUtil.getOrCreateCorrelationId();
        log.info("Creating new user with username: {} and correlation ID: {}", request.getUsername(), correlationId);

        return observabilityUtil.timeOperation("user.create", () -> {
            validateUserCreation(request);

            // Check for existing username and email
            validateUsernameUniqueness(request.getUsername(), null);
            validateEmailUniqueness(request.getEmail(), null);

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

                // Send message to queue for background processing
                try {
                    UserMessageDTO userMessage = UserMessageDTO.builder()
                            .id(savedUser.getId())
                            .username(savedUser.getUsername())
                            .email(savedUser.getEmail())
                            .firstName(savedUser.getFirstName())
                            .lastName(savedUser.getLastName())
                            .action("CREATE")
                            .build();

                    messageQueueService.sendUserMessage(userMessage);
                } catch (Exception e) {
                    log.error("Error sending user creation message to queue: {}", e.getMessage(), e);
                    // Don't throw exception here as it shouldn't affect the main operation
                }

                return userMapper.toUserResponse(savedUser);
            } catch (DataIntegrityViolationException e) {
                log.error("Error creating user due to data integrity violation: {} | Correlation ID: {}",
                        e.getMessage(), correlationId);
                observabilityUtil.recordError("user.create", "DATA_INTEGRITY_VIOLATION");
                throw new AppException(ErrorCode.DATABASE_002, "Username or email already exists");
            }
        }, "operation", "create");
    }

    @Override
    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        String correlationId = RequestTracingUtil.getOrCreateCorrelationId();
        log.info("Updating user with ID: {} using update request | Correlation ID: {}", id, correlationId);

        return observabilityUtil.timeOperation("user.update", () -> {
            validateUserUpdate(id, request);

            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            // Store old values for audit log
            String oldUsername = existingUser.getUsername();
            String oldEmail = existingUser.getEmail();
            String oldFirstName = existingUser.getFirstName();
            String oldLastName = existingUser.getLastName();

            // Validate uniqueness (excluding current user)
            if (request.getUsername() != null) {
                validateUsernameUniqueness(request.getUsername(), id);
                existingUser.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                validateEmailUniqueness(request.getEmail(), id);
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

                // Send message to queue for background processing
                try {
                    UserMessageDTO userMessage = UserMessageDTO.builder()
                            .id(updatedUser.getId())
                            .username(updatedUser.getUsername())
                            .email(updatedUser.getEmail())
                            .firstName(updatedUser.getFirstName())
                            .lastName(updatedUser.getLastName())
                            .action("UPDATE")
                            .build();

                    messageQueueService.sendUserMessage(userMessage);
                } catch (Exception e) {
                    log.error("Error sending user update message to queue: {}", e.getMessage(), e);
                    // Don't throw exception here as it shouldn't affect the main operation
                }

                return userMapper.toUserResponse(updatedUser);
            } catch (DataIntegrityViolationException e) {
                log.error("Error updating user due to data integrity violation: {} | Correlation ID: {}",
                        e.getMessage(), correlationId);
                observabilityUtil.recordError("user.update", "DATA_INTEGRITY_VIOLATION");
                throw new AppException(ErrorCode.DATABASE_002, "Username or email already exists");
            }
        }, "operation", "update", "userId", id.toString());
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(String username, UserUpdateRequest request) {
        log.debug("Updating current user: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // For current user update, we don't validate uniqueness against themselves
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            validateUsernameUniqueness(request.getUsername(), user.getId());
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            validateEmailUniqueness(request.getEmail(), user.getId());
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        if (request.getPhone() != null)
            user.setPhone(request.getPhone());
        if (request.getAddress() != null)
            user.setAddress(request.getAddress());

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Current user updated successfully: {}", updatedUser.getUsername());
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Cacheable(value = "users", key = "'username_' + #username")
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @Cacheable(value = "users", key = "'email_' + #email")
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
    public void validateUserCreation(UserCreateRequest request) {
        log.debug("Validating user creation request");

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_002, "Username is required");
        }

        // Sanitize and validate username
        String sanitizedUsername = securityUtil.sanitizeUserInput(request.getUsername());
        if (!securityUtil.isValidUsername(sanitizedUsername)) {
            throw new AppException(ErrorCode.VALIDATION_002, "Username contains invalid characters or format");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_002, "Email is required");
        }

        // Validate email format
        if (!securityUtil.isValidEmail(request.getEmail())) {
            throw new AppException(ErrorCode.VALIDATION_003, "Invalid email format");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new AppException(ErrorCode.VALIDATION_004, "Password must be at least 6 characters");
        }
    }

    @Override
    public void validateUserUpdate(Long id, UserUpdateRequest request) {
        log.debug("Validating user update request for user ID: {}", id);
        if (id == null) {
            throw new AppException(ErrorCode.USER_005, "User ID is required");
        }

        if (request.getUsername() != null) {
            if (request.getUsername().trim().isEmpty()) {
                throw new AppException(ErrorCode.VALIDATION_002, "Username cannot be empty");
            }

            // Sanitize and validate username
            String sanitizedUsername = securityUtil.sanitizeUserInput(request.getUsername());
            if (!securityUtil.isValidUsername(sanitizedUsername)) {
                throw new AppException(ErrorCode.VALIDATION_002, "Username contains invalid characters or format");
            }
        }

        if (request.getEmail() != null) {
            if (request.getEmail().trim().isEmpty()) {
                throw new AppException(ErrorCode.VALIDATION_002, "Email cannot be empty");
            }

            // Validate email format
            if (!securityUtil.isValidEmail(request.getEmail())) {
                throw new AppException(ErrorCode.VALIDATION_003, "Invalid email format");
            }
        }
    }

    @Override
    public void validateUsernameUniqueness(String username, Long excludeId) {
        log.debug("Validating username uniqueness for: {} excluding ID: {}", username, excludeId);
        boolean exists = userRepository.existsByUsername(username);
        if (exists && excludeId != null) {
            // If we're updating, check if the existing user is not the one we're updating
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeId)) {
                throw new AppException(ErrorCode.USER_002, "Username already exists");
            }
        } else if (exists && excludeId == null) {
            // If we're creating, just check if it exists
            throw new AppException(ErrorCode.USER_002, "Username already exists");
        }
    }

    @Override
    public void validateEmailUniqueness(String email, Long excludeId) {
        log.debug("Validating email uniqueness for: {} excluding ID: {}", email, excludeId);
        boolean exists = userRepository.existsByEmail(email);
        if (exists && excludeId != null) {
            // If we're updating, check if the existing user is not the one we're updating
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeId)) {
                throw new AppException(ErrorCode.USER_003, "Email already exists");
            }
        } else if (exists && excludeId == null) {
            // If we're creating, just check if it exists
            throw new AppException(ErrorCode.USER_003, "Email already exists");
        }
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
        return userMapper.toUserResponse(updatedUser);
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
        return userMapper.toUserResponse(updatedUser);
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
        return userMapper.toUserResponse(updatedUser);
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
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProjection> getAllUsersProjected(Pageable pageable) {
        log.debug("Getting users with projection and pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProjection> getUserProjectedById(Long id) {
        log.debug("Getting user with projection by id: {}", id);
        return userRepository.findProjectedById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProjection> getUserProjectedByUsername(String username) {
        log.debug("Getting user with projection by username: {}", username);
        return userRepository.findProjectedByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProjection> getUserProjectedByEmail(String email) {
        log.debug("Getting user with projection by email: {}", email);
        return userRepository.findProjectedByEmail(email);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        // Instead of actually deleting, we'll soft delete by setting deletedAt
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User soft-deleted successfully: ID={}", id);

        // Send message to queue for background processing
        try {
            UserMessageDTO userMessage = UserMessageDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .action("DELETE")
                    .build();

            messageQueueService.sendUserMessage(userMessage);
        } catch (Exception e) {
            log.error("Error sending user deletion message to queue: {}", e.getMessage(), e);
            // Don't throw exception here as it shouldn't affect the main operation
        }
    }
}