package com.myproject.nexa.controllers.admin;

import com.myproject.nexa.entities.Tenant;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.services.TenantService;
import com.myproject.nexa.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminDashboardController {

    private final TenantService tenantService;
    private final UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        log.info("Retrieving system statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Tenant statistics
        List<Tenant> allTenants = tenantService.findAll();
        stats.put("totalTenants", allTenants.size());
        long activeTenants = allTenants.stream()
                .filter(tenant -> tenant.getStatus() == Tenant.TenantStatus.ACTIVE)
                .count();
        stats.put("activeTenants", activeTenants);
        
        // User statistics
        List<User> allUsers = userService.findAll();
        stats.put("totalUsers", allUsers.size());
        long activeUsers = allUsers.stream()
                .filter(User::getEnabled)
                .count();
        stats.put("activeUsers", activeUsers);
        
        // Additional metrics can be added here
        stats.put("inactiveTenants", allTenants.size() - activeTenants);
        stats.put("inactiveUsers", allUsers.size() - activeUsers);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity() {
        log.info("Retrieving recent activity");
        
        Map<String, Object> activity = new HashMap<>();
        
        // For now, we'll return placeholder data
        // In a real implementation, this would query audit logs or activity tables
        activity.put("recentUserRegistrations", 0);
        activity.put("recentTenantCreations", 0);
        activity.put("recentFailedLogins", 0);
        activity.put("systemHealth", "OK");
        
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/tenants-overview")
    public ResponseEntity<Map<String, Object>> getTenantsOverview() {
        log.info("Retrieving tenants overview");
        
        List<Tenant> tenants = tenantService.findAll();
        Map<String, Object> overview = new HashMap<>();
        
        overview.put("tenants", tenants);
        overview.put("total", tenants.size());
        
        // Group by status
        Map<Tenant.TenantStatus, Long> statusCounts = tenants.stream()
                .collect(java.util.stream.Collectors.groupingBy(Tenant::getStatus, 
                        java.util.stream.Collectors.counting()));
        overview.put("statusBreakdown", statusCounts);
        
        return ResponseEntity.ok(overview);
    }
}