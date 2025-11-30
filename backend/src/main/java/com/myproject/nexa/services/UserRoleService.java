package com.myproject.nexa.services;

import com.myproject.nexa.dto.response.RoleResponse;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.Role;
import com.myproject.nexa.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for role management operations
 * Handles role assignments and permissions
 */
public interface UserRoleService {
    
    /**
     * Get all roles
     */
    List<RoleResponse> getAllRoles();
    
    /**
     * Get role by name
     */
    Optional<Role> findRoleByName(String name);
    
    /**
     * Assign role to user
     */
    UserResponse assignRoleToUser(Long userId, String roleName);
    
    /**
     * Remove role from user
     */
    UserResponse removeRoleFromUser(Long userId, String roleName);
    
    /**
     * Get all roles for a user
     */
    List<RoleResponse> getUserRoles(Long userId);
}