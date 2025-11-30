package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FutureInnovationResponse {
    private String domain;
    private Integer innovationScore;
    private Integer futureReadinessScore;
    private Integer technologyModernizationLevel;
    private Integer competitiveInnovationAdvantage;
    private Map<String, Object> innovationTimeline;
    private List<String> cuttingEdgeFeatures;
    private Map<String, Object> futureTechnologyAdoption;
    private Map<String, Object> innovationMetrics;
    private Map<String, Object> researchDevelopmentInvestment;
}