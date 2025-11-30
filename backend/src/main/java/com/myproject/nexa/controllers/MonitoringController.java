package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.BusinessMetricsResponse;
import com.myproject.nexa.dto.response.InnovationIndicatorsResponse;
import com.myproject.nexa.dto.response.MonitoringDashboardResponse;
import com.myproject.nexa.services.MonitoringDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring & Observability", description = "APIs for monitoring and observability features")
public class MonitoringController {

    private final MonitoringDashboardService monitoringDashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get comprehensive monitoring dashboard")
    public ResponseEntity<MonitoringDashboardResponse> getMonitoringDashboard(
            @RequestParam(defaultValue = "default") String tenantId) {
        MonitoringDashboardResponse response = monitoringDashboardService.getMonitoringDashboard(tenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business-metrics")
    @Operation(summary = "Get business-specific metrics")
    public ResponseEntity<BusinessMetricsResponse> getBusinessMetrics() {
        BusinessMetricsResponse response = monitoringDashboardService.getBusinessMetrics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/innovation-indicators")
    @Operation(summary = "Get innovation indicators showing future-first features")
    public ResponseEntity<InnovationIndicatorsResponse> getInnovationIndicators() {
        InnovationIndicatorsResponse response = monitoringDashboardService.getInnovationIndicators();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/business-metric")
    @Operation(summary = "Log a custom business metric")
    public ResponseEntity<Void> logBusinessMetric(
            @RequestParam String metricName,
            @RequestParam double value,
            @RequestParam(required = false) String[] tags) {
        monitoringDashboardService.logBusinessMetric(metricName, value, tags != null ? tags : new String[]{});
        return ResponseEntity.ok().build();
    }

    @PostMapping("/track-business-event")
    @Operation(summary = "Track a business event with tracing")
    public ResponseEntity<Void> trackBusinessEvent(
            @RequestParam String eventName,
            @RequestBody Map<String, String> attributes) {
        monitoringDashboardService.trackBusinessEvent(eventName, attributes);
        return ResponseEntity.ok().build();
    }
}