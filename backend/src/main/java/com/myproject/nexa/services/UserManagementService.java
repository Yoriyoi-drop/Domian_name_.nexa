package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations including CRUD operations,
 * account management (enable/disable/lock/unlock), and role assignments.
 */
public interface UserManagementService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String username);
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    void deleteUser(Long id);

    // Account management methods
    UserResponse enableUser(Long id);
    UserResponse disableUser(Long id);
    UserResponse lockUserAccount(Long id);
    UserResponse unlockUserAccount(Long id);

    // Search methods
    List<UserResponse> findByRole(String roleName);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}