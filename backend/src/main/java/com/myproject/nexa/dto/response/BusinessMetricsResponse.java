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
public class BusinessMetricsResponse {
    private Double userRegistrations;
    private Double userLogins;
    private Double apiCalls;
    private Double errorRate;
    private Map<String, Object> businessKpis;
    private Map<String, Object> trendIndicators;
}