package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User, Long, UserResponse> {
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String username);
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(Long id, User user);
    UserResponse mapToUserResponse(User user);
    void deleteUser(Long id);

    // Business logic methods
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse updateCurrentUser(String username, UserUpdateRequest request);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserResponse> findByRole(String roleName);

    // Business validation methods
    void validateUserCreation(UserCreateRequest request);
    void validateUserUpdate(Long id, UserUpdateRequest request);
    void validateUsernameUniqueness(String username, Long excludeId);
    void validateEmailUniqueness(String email, Long excludeId);

    // Business operation methods
    UserResponse enableUser(Long id);
    UserResponse disableUser(Long id);
    UserResponse lockUserAccount(Long id);
    UserResponse unlockUserAccount(Long id);
}