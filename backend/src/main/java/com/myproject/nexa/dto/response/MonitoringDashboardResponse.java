package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringDashboardResponse {
    private String tenantId;
    private LocalDateTime timestamp;
    private Map<String, Object> systemMetrics;
    private Map<String, Object> businessMetrics;
    private Map<String, Object> tracingInfo;
    private Map<String, Object> innovationIndicators;
    private Map<String, Object> healthStatus;
    private Map<String, Object> performanceIndicators;
}