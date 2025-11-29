package com.myproject.nexa.services.impl;

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
import com.myproject.nexa.services.UserService;
import com.myproject.nexa.utils.AuditLogUtil;
import com.myproject.nexa.utils.ObservabilityUtil;
import com.myproject.nexa.utils.RequestTracingUtil;
import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserResponse, UserRepository> implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObservabilityUtil observabilityUtil;
    private final AuditLogUtil auditLogUtil;
    private final SecurityUtil securityUtil;

    public UserServiceImpl(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          ObservabilityUtil observabilityUtil,
                          AuditLogUtil auditLogUtil,
                          SecurityUtil securityUtil) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.observabilityUtil = observabilityUtil;
        this.auditLogUtil = auditLogUtil;
        this.securityUtil = securityUtil;
    }

    // Override the inherited abstract methods from BaseServiceImpl

    @Override
    protected UserResponse mapToResponse(User entity) {
        return mapToUserResponse(entity);
    }

    @Override
    public UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        log.debug("Getting current user: {}", username);
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Getting all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Getting users with pagination: page={}, size={}",
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
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
        return mapToUserResponse(updatedUser);
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

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();
            user.setCreatedAt(LocalDateTime.now());

            // Assign default USER role
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Default USER role not found"));
            user.setRoles(List.of(userRole));

            try {
                User savedUser = userRepository.save(user);
                log.info("User created successfully with ID: {} and correlation ID: {}", savedUser.getId(), correlationId);

                // Record audit log
                auditLogUtil.logUserAction(
                    savedUser.getId().toString(),
                    "USER_CREATE",
                    "User",
                    Map.of("username", savedUser.getUsername(), "email", savedUser.getEmail())
                );

                // Record business metric
                observabilityUtil.recordBusinessMetric("user.count", "create", 1.0, "operation", "create");

                return mapToUserResponse(savedUser);
            } catch (DataIntegrityViolationException e) {
                log.error("Error creating user due to data integrity violation: {} | Correlation ID: {}", e.getMessage(), correlationId);
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
            if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
            if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
            if (request.getAddress() != null) existingUser.setAddress(request.getAddress());
            if (request.getEnabled() != null) existingUser.setEnabled(request.getEnabled());

            existingUser.setUpdatedAt(LocalDateTime.now());

            try {
                User updatedUser = userRepository.save(existingUser);
                log.info("User updated successfully with ID: {} | Correlation ID: {}", updatedUser.getId(), correlationId);

                // Record audit log
                auditLogUtil.logUserAction(
                    updatedUser.getId().toString(),
                    "USER_UPDATE",
                    "User",
                    Map.of(
                        "oldUsername", oldUsername,
                        "newUsername", updatedUser.getUsername(),
                        "oldEmail", oldEmail,
                        "newEmail", updatedUser.getEmail()
                    )
                );

                // Record business metric
                observabilityUtil.recordBusinessMetric("user.count", "update", 1.0, "operation", "update");

                return mapToUserResponse(updatedUser);
            } catch (DataIntegrityViolationException e) {
                log.error("Error updating user due to data integrity violation: {} | Correlation ID: {}", e.getMessage(), correlationId);
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
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Current user updated successfully: {}", updatedUser.getUsername());
        return mapToUserResponse(updatedUser);
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

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(String roleName) {
        log.debug("Finding users by role: {}", roleName);
        List<User> users = userRepository.findByRoleName(roleName);
        return users.stream()
                .map(this::mapToUserResponse)
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
        return mapToUserResponse(updatedUser);
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
        return mapToUserResponse(updatedUser);
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
        return mapToUserResponse(updatedUser);
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
        return mapToUserResponse(updatedUser);
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

        log.info("User soft-deleted successfully: ID={}", id);
    }
}