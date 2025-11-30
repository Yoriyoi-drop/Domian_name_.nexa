package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.request.AssignUserToTenantRequest;
import com.myproject.nexa.dto.request.CreateTenantRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.TenantResponse;
import com.myproject.nexa.dto.response.TenantUserResponse;
import com.myproject.nexa.services.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tenant Management", description = "APIs for multi-tenancy management")
public class TenantController {
    
    private final TenantService tenantService;
    
    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Create a new tenant in the system")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        log.info("Creating new tenant: {}", request.getName());
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.ok(ApiResponse.success("Tenant created successfully", response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Retrieve tenant information by its ID")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantById(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id) {
        log.debug("Getting tenant by ID: {}", id);
        TenantResponse response = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }
    
    @GetMapping("/subdomain/{subdomain}")
    @Operation(summary = "Get tenant by subdomain", description = "Retrieve tenant information by its subdomain")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenantBySubdomain(
            @Parameter(description = "Tenant subdomain", required = true) @PathVariable String subdomain) {
        log.debug("Getting tenant by subdomain: {}", subdomain);
        TenantResponse response = tenantService.getTenantBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all tenants", description = "Retrieve a paginated list of all tenants")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getAllTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TenantResponse> tenants = tenantService.getAllTenants(pageable);
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", tenants));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update tenant", description = "Update an existing tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CreateTenantRequest request) {
        log.info("Updating tenant with ID: {}", id);
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant", description = "Soft-delete a tenant from the system")
    public ResponseEntity<ApiResponse<Object>> deleteTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id) {
        log.info("Deleting tenant with ID: {}", id);
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully"));
    }
    
    @PostMapping("/{id}/users")
    @Operation(summary = "Assign user to tenant", description = "Assign a user to a specific tenant")
    public ResponseEntity<ApiResponse<TenantUserResponse>> assignUserToTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AssignUserToTenantRequest request) {
        log.info("Assigning user to tenant: {}", id);
        request.setTenantId(id);
        TenantUserResponse response = tenantService.assignUserToTenant(request);
        return ResponseEntity.ok(ApiResponse.success("User assigned to tenant successfully", response));
    }
    
    @DeleteMapping("/{tenantId}/users/{userId}")
    @Operation(summary = "Remove user from tenant", description = "Remove a user from a specific tenant")
    public ResponseEntity<ApiResponse<TenantUserResponse>> removeUserFromTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long tenantId,
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        log.info("Removing user {} from tenant {}", userId, tenantId);
        TenantUserResponse response = tenantService.removeUserFromTenant(tenantId, userId);
        return ResponseEntity.ok(ApiResponse.success("User removed from tenant successfully", response));
    }
    
    @GetMapping("/{id}/users")
    @Operation(summary = "Get users in tenant", description = "Retrieve all users assigned to a specific tenant")
    public ResponseEntity<ApiResponse<List<TenantUserResponse>>> getTenantUsers(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id) {
        log.debug("Getting users for tenant: {}", id);
        List<TenantUserResponse> users = tenantService.getTenantUsers(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant users retrieved successfully", users));
    }
    
    @GetMapping("/users/{userId}/tenants")
    @Operation(summary = "Get user's tenants", description = "Retrieve all tenants where a specific user is assigned")
    public ResponseEntity<ApiResponse<List<TenantUserResponse>>> getUserTenants(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        log.debug("Getting tenants for user: {}", userId);
        List<TenantUserResponse> tenants = tenantService.getUserTenants(userId);
        return ResponseEntity.ok(ApiResponse.success("User tenants retrieved successfully", tenants));
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate tenant", description = "Activate an inactive tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> activateTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id) {
        log.info("Activating tenant with ID: {}", id);
        TenantResponse response = tenantService.activateTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant activated successfully", response));
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate tenant", description = "Deactivate an active tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> deactivateTenant(
            @Parameter(description = "Tenant ID", required = true) @PathVariable Long id) {
        log.info("Deactivating tenant with ID: {}", id);
        TenantResponse response = tenantService.deactivateTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deactivated successfully", response));
    }
}