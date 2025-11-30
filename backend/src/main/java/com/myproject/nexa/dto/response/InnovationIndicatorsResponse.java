package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnovationIndicatorsResponse {
    private Integer domainInnovationScore;
    private Integer technologyStackModern;
    private Integer performanceOptimization;
    private Integer securityInnovation;
    private Integer ecosystemIntegration;
    private Integer futureProofing;
    private Map<String, Object> innovationMetrics;
    private List<String> competitiveAdvantages;
}