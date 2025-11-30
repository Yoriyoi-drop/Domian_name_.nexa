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
public class EcosystemAnalyticsResponse {
    private String domain;
    private Long totalReferrals;
    private Map<String, Object> marketplaceActivity;
    private Integer integrationCount;
    private Map<String, Object> communityEngagement;
    private Map<String, Object> premiumFeatureAdoption;
    private Map<String, Object> ecosystemGrowthMetrics;
    private List<String> partnershipOpportunities;
}