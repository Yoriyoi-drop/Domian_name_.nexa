package com.myproject.nexa.controllers.admin;

import com.myproject.nexa.dto.request.CreateTenantRequest;
import com.myproject.nexa.dto.response.TenantResponse;
import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.services.TenantService;
import com.myproject.nexa.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTenantController {

    private final TenantService tenantService;
    private final UserService userService;

    @GetMapping("/tenants")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.findAll();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/tenants/{id}")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable Long id) {
        TenantResponse response = tenantService.getTenantById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/tenants")
    public ResponseEntity<TenantResponse> createTenant(@RequestBody CreateTenantRequest request) {
        // The method in service interface expects CreateTenantRequest
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tenants/{id}/status")
    public ResponseEntity<Void> updateTenantStatus(@PathVariable Long id, @RequestBody UpdateTenantStatusRequest request) {
        tenantService.updateTenantStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tenants/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tenants/{tenantId}/users")
    public ResponseEntity<List<User>> getTenantUsers(@PathVariable Long tenantId) {
        // This would need a method in UserService to get users by tenant
        // For now, return all users (in a real implementation, filter by tenant)
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // Inner request classes
    public static class CreateTenantRequest {
        private String name;
        private String subdomain;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
    }

    public static class UpdateTenantStatusRequest {
        private Tenant.TenantStatus status;

        // Getters and setters
        public Tenant.TenantStatus getStatus() { return status; }
        public void setStatus(Tenant.TenantStatus status) { this.status = status; }
    }
}