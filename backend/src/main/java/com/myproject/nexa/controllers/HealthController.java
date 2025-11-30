package com.myproject.nexa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health Check", description = "Health check API")
public class HealthController {

    @Value("${app.name:MyProject.nexa}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${spring.profiles.active:unknown}")
    private String profile;

    @GetMapping
    @Operation(summary = "Health check", description = "Check application health status")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Health check requested");
        
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "app", appName,
            "version", appVersion,
            "profile", profile,
            "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "details", Map.of(
                "database", Map.of("status", "UP"),
                "redis", Map.of("status", "UP"),
                "diskSpace", Map.of("status", "UP", "total", "10GB", "free", "5GB")
            )
        );

        return ResponseEntity.ok(healthInfo);
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Check application readiness for serving requests")
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        log.info("Readiness check requested");
        
        Map<String, Object> readinessInfo = Map.of(
            "status", "READY",
            "app", appName,
            "version", appVersion,
            "profile", profile,
            "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        return ResponseEntity.ok(readinessInfo);
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness check", description = "Check application liveness")
    public ResponseEntity<Map<String, Object>> livenessCheck() {
        log.info("Liveness check requested");
        
        Map<String, Object> livenessInfo = Map.of(
            "status", "ALIVE",
            "app", appName,
            "version", appVersion,
            "profile", profile,
            "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        return ResponseEntity.ok(livenessInfo);
    }
}