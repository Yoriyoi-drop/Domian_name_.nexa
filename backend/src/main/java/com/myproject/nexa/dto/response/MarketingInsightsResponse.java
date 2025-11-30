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
public class MarketingInsightsResponse {
    private String domain;
    private String userId;
    private Map<String, Object> trafficAnalytics;
    private Map<String, Object> conversionMetrics;
    private Map<String, Object> userJourneyInsights;
    private Map<String, Object> campaignPerformance;
    private Map<String, Object> seoMetrics;
    private Map<String, Object> socialMediaMetrics;
    private Map<String, Object> roiCalculations;
    private Map<String, Object> growthPredictions;
}