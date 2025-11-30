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
public class ReferralProgramResponse {
    private String userId;
    private String campaignId;
    private String referralCode;
    private Long currentReferrals;
    private Double rewardsEarned;
    private Double rewardsPending;
    private String referralLink;
    private List<String> programBenefits;
    private Map<String, Object> performanceMetrics;
}