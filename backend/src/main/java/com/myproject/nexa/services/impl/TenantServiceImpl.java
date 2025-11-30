package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.request.AssignUserToTenantRequest;
import com.myproject.nexa.dto.request.CreateTenantRequest;
import com.myproject.nexa.dto.response.TenantResponse;
import com.myproject.nexa.dto.response.TenantUserResponse;
import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.TenantUser;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.TenantRepository;
import com.myproject.nexa.repositories.TenantUserRepository;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {
        log.info("Creating new tenant with name: {} and subdomain: {}", request.getName(), request.getSubdomain());

        // Check if tenant with same name or subdomain already exists
        if (tenantRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.RESOURCE_002, "Tenant with this name already exists");
        }

        if (tenantRepository.findBySubdomain(request.getSubdomain()).isPresent()) {
            throw new AppException(ErrorCode.RESOURCE_002, "Tenant with this subdomain already exists");
        }

        // Create new tenant
        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .subdomain(request.getSubdomain())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .status(Tenant.TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant created successfully with ID: {}", savedTenant.getId());

        return TenantResponse.fromEntity(savedTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getTenantById(Long id) {
        log.debug("Getting tenant by ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + id));
        return TenantResponse.fromEntity(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getTenantBySubdomain(String subdomain) {
        log.debug("Getting tenant by subdomain: {}", subdomain);
        Tenant tenant = tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001,
                        "Tenant not found with subdomain: " + subdomain));
        return TenantResponse.fromEntity(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantResponse> getAllTenants(Pageable pageable) {
        log.debug("Getting all tenants with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return tenantRepository.findAll(pageable).map(TenantResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants() {
        log.debug("Getting all tenants");
        return tenantRepository.findAll().stream()
                .map(TenantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantResponse updateTenant(Long id, CreateTenantRequest request) {
        log.info("Updating tenant with ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + id));

        // Check for name/subdomain conflicts if they are being changed
        if (!tenant.getName().equals(request.getName()) &&
                tenantRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.RESOURCE_002, "Tenant with this name already exists");
        }

        if (!tenant.getSubdomain().equals(request.getSubdomain()) &&
                tenantRepository.findBySubdomain(request.getSubdomain()).isPresent()) {
            throw new AppException(ErrorCode.RESOURCE_002, "Tenant with this subdomain already exists");
        }

        tenant.setName(request.getName());
        tenant.setSubdomain(request.getSubdomain());
        tenant.setDisplayName(request.getDisplayName());
        tenant.setDescription(request.getDescription());
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Tenant updated successfully: ID={}", updatedTenant.getId());

        return TenantResponse.fromEntity(updatedTenant);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        log.info("Deleting tenant with ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + id));

        // Soft delete - mark as deleted
        tenant.setStatus(Tenant.TenantStatus.DELETED);
        tenant.setDeletedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());

        tenantRepository.save(tenant);
        log.info("Tenant soft deleted successfully: ID={}", id);
    }

    @Override
    @Transactional
    public TenantUserResponse assignUserToTenant(AssignUserToTenantRequest request) {
        log.info("Assigning user {} to tenant {}", request.getUserId(), request.getTenantId());

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001,
                        "Tenant not found with id: " + request.getTenantId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(
                        () -> new AppException(ErrorCode.USER_001, "User not found with id: " + request.getUserId()));

        // Check if user is already assigned to this tenant
        if (tenantUserRepository.existsByTenantAndUser(tenant, user)) {
            throw new AppException(ErrorCode.RESOURCE_002, "User is already assigned to this tenant");
        }

        // Create new tenant-user relationship
        TenantUser tenantUser = TenantUser.builder()
                .tenant(tenant)
                .user(user)
                .role(request.getRole() != null ? TenantUser.TenantUserRole.valueOf(request.getRole())
                        : TenantUser.TenantUserRole.MEMBER)
                .isActive(true)
                .assignedAt(LocalDateTime.now())
                .build();

        TenantUser savedTenantUser = tenantUserRepository.save(tenantUser);
        log.info("User {} assigned to tenant {} successfully", request.getUserId(), request.getTenantId());

        return TenantUserResponse.fromEntity(savedTenantUser);
    }

    @Override
    @Transactional
    public TenantUserResponse removeUserFromTenant(Long tenantId, Long userId) {
        log.info("Removing user {} from tenant {}", userId, tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + tenantId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_001, "User not found with id: " + userId));

        TenantUser tenantUser = tenantUserRepository.findByTenantAndUser(tenant, user)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "User is not assigned to this tenant"));

        // Instead of hard delete, mark as inactive
        tenantUser.setActive(false);
        tenantUser.setRemovedAt(LocalDateTime.now());

        TenantUser updatedTenantUser = tenantUserRepository.save(tenantUser);
        log.info("User {} removed from tenant {} successfully", userId, tenantId);

        return TenantUserResponse.fromEntity(updatedTenantUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantUserResponse> getTenantUsers(Long tenantId) {
        log.debug("Getting all users for tenant: {}", tenantId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + tenantId));

        return tenantUserRepository.findByTenant(tenant).stream()
                .filter(TenantUser::isActive) // Only active assignments
                .map(TenantUserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantUserResponse> getUserTenants(Long userId) {
        log.debug("Getting all tenants for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_001, "User not found with id: " + userId));

        return tenantUserRepository.findByUser(user).stream()
                .filter(TenantUser::isActive) // Only active assignments
                .map(TenantUserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantResponse activateTenant(Long id) {
        log.info("Activating tenant with ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + id));

        if (tenant.getStatus() == Tenant.TenantStatus.ACTIVE) {
            throw new AppException(ErrorCode.BUSINESS_002, "Tenant is already active");
        }

        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Tenant activated successfully: ID={}", updatedTenant.getId());

        return TenantResponse.fromEntity(updatedTenant);
    }

    @Override
    @Transactional
    public TenantResponse deactivateTenant(Long id) {
        log.info("Deactivating tenant with ID: {}", id);
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_001, "Tenant not found with id: " + id));

        if (tenant.getStatus() == Tenant.TenantStatus.INACTIVE) {
            throw new AppException(ErrorCode.BUSINESS_002, "Tenant is already inactive");
        }

        tenant.setStatus(Tenant.TenantStatus.INACTIVE);
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("Tenant deactivated successfully: ID={}", updatedTenant.getId());

        return TenantResponse.fromEntity(updatedTenant);
    }

    @Override
    @Transactional
    public void updateTenantStatus(Long tenantId, Tenant.TenantStatus status) {
        log.info("Updating tenant {} status to: {}", tenantId, status);
        Optional<Tenant> tenantOpt = tenantRepository.findById(tenantId);
        if (tenantOpt.isPresent()) {
            Tenant tenant = tenantOpt.get();
            tenant.setStatus(status);
            tenant.setUpdatedAt(LocalDateTime.now());
            tenantRepository.save(tenant);
            log.info("Updated tenant {} status successfully", tenantId);
        } else {
            throw new AppException(ErrorCode.RESOURCE_001, "Tenant with ID " + tenantId + " not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tenantRepository.findByName(name).isPresent();
    }
}