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
public class PremiumMarketplaceResponse {
    private Integer page;
    private Integer size;
    private Long totalListings;
    private List<Map<String, Object>> availableDomains;
    private List<Map<String, Object>> featuredListings;
    private Map<String, Object> marketTrends;
    private Map<String, Object> pricingAnalytics;
    private Map<String, Object> marketplaceInsights;
}