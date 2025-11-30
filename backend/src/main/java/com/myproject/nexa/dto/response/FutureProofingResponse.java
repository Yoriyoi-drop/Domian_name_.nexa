package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FutureProofingResponse {
    private String domain;
    private Integer scalabilityScore;
    private Integer securityEvolutionScore;
    private Integer technologyLongevity;
    private Integer standardsCompliance;
    private Integer interoperabilityScore;
    private Integer migrationReadiness;
    private Map<String, Object> futureTrendAlignment;
    private Map<String, Object> adaptabilityIndicators;
}