package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard API")
public class DashboardController {

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        // Mock data - in a real implementation, this would fetch from a service
        Map<String, Object> stats = Map.of(
            "totalUsers", 1234,
            "activeSessions", 456,
            "revenue", 12345.67,
            "growth", 12.5
        );
        
        return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", stats));
    }

    @GetMapping("/activity")
    @Operation(summary = "Get recent activity", description = "Retrieve recent activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentActivity() {
        // Mock data - in a real implementation, this would fetch from a service
        Map<String, Object> activity = Map.of(
            "recentUsers", 15,
            "recentLogins", 32,
            "recentActions", 45
        );
        
        return ResponseEntity.ok(ApiResponse.success("Activity retrieved successfully", activity));
    }
}