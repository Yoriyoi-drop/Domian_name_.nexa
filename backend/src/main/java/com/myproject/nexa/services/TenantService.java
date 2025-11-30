package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.AssignUserToTenantRequest;
import com.myproject.nexa.dto.request.CreateTenantRequest;
import com.myproject.nexa.dto.response.TenantResponse;
import com.myproject.nexa.dto.response.TenantUserResponse;
import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.TenantUser;
import com.myproject.nexa.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);
    TenantResponse getTenantById(Long id);
    TenantResponse getTenantBySubdomain(String subdomain);
    Page<TenantResponse> getAllTenants(Pageable pageable);
    List<TenantResponse> getAllTenants();
    List<Tenant> findAll();
    Optional<Tenant> findBySubdomain(String subdomain);
    TenantResponse updateTenant(Long id, CreateTenantRequest request);
    void deleteTenant(Long id);
    TenantUserResponse assignUserToTenant(AssignUserToTenantRequest request);
    TenantUserResponse removeUserFromTenant(Long tenantId, Long userId);
    List<TenantUserResponse> getTenantUsers(Long tenantId);
    List<TenantUserResponse> getUserTenants(Long userId);
    TenantResponse activateTenant(Long id);
    TenantResponse deactivateTenant(Long id);
    boolean existsBySubdomain(String subdomain);
    boolean existsByName(String name);
    void updateTenantStatus(Long tenantId, Tenant.TenantStatus status);
}