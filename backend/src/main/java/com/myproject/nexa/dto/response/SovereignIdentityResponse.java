package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SovereignIdentityResponse {
    private String domain;
    private String sovereignStatus;
    private String geopoliticalRiskLevel;
    private String jurisdiction;
    private String governanceModel;
    private List<String> securityCompliance;
    private Map<String, Object> availabilityMetrics;
    private Map<String, Object> trustIndicators;
}