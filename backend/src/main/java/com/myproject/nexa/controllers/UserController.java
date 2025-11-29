package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.services.UserConcurrencyService;
import com.myproject.nexa.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;
    private final UserConcurrencyService userConcurrencyService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve the currently authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse user = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved successfully", user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update a user by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update the currently authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse updatedUser = userService.updateCurrentUser(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Current user updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Enable user", description = "Enable a user account")
    public ResponseEntity<ApiResponse<UserResponse>> enableUser(@PathVariable Long id) {
        UserResponse user = userService.enableUser(id);
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully", user));
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Disable user", description = "Disable a user account")
    public ResponseEntity<ApiResponse<UserResponse>> disableUser(@PathVariable Long id) {
        UserResponse user = userService.disableUser(id);
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully", user));
    }

    @PatchMapping("/{id}/lock")
    @Operation(summary = "Lock user account", description = "Lock a user account")
    public ResponseEntity<ApiResponse<UserResponse>> lockUserAccount(@PathVariable Long id) {
        UserResponse user = userService.lockUserAccount(id);
        return ResponseEntity.ok(ApiResponse.success("User account locked successfully", user));
    }

    @PatchMapping("/{id}/unlock")
    @Operation(summary = "Unlock user account", description = "Unlock a user account")
    public ResponseEntity<ApiResponse<UserResponse>> unlockUserAccount(@PathVariable Long id) {
        UserResponse user = userService.unlockUserAccount(id);
        return ResponseEntity.ok(ApiResponse.success("User account unlocked successfully", user));
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Get users by role", description = "Retrieve all users with a specific role")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable String roleName) {
        List<UserResponse> users = userService.findByRole(roleName);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @PatchMapping("/{id}/concurrent-update")
    @Operation(summary = "Concurrent-safe user update", description = "Update user with distributed locking to prevent conflicts")
    public ResponseEntity<ApiResponse<UserResponse>> updateUsersConcurrentSafe(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse user = userConcurrencyService
            .updateUserWithDistributedLock(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully with concurrency protection", user));
    }
}