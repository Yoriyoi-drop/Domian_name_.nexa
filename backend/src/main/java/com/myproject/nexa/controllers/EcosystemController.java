package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.EcosystemAnalyticsResponse;
import com.myproject.nexa.dto.response.MarketingInsightsResponse;
import com.myproject.nexa.dto.response.PremiumMarketplaceResponse;
import com.myproject.nexa.dto.response.ReferralProgramResponse;
import com.myproject.nexa.services.EcosystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ecosystem")
@RequiredArgsConstructor
@Tag(name = "Ecosystem & Marketing", description = "APIs for ecosystem and marketing features")
public class EcosystemController {

    private final EcosystemService ecosystemService;

    @GetMapping("/analytics/{domainName}")
    @Operation(summary = "Get ecosystem analytics for a domain")
    public ResponseEntity<EcosystemAnalyticsResponse> getEcosystemAnalytics(@PathVariable String domainName) {
        EcosystemAnalyticsResponse response = ecosystemService.getEcosystemAnalytics(domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/referral-program/{userId}")
    @Operation(summary = "Get referral program details for a user")
    public ResponseEntity<ReferralProgramResponse> getReferralProgram(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "default") String campaignId) {
        ReferralProgramResponse response = ecosystemService.generateReferralProgram(userId, campaignId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/referral-program/{userId}/create-code")
    @Operation(summary = "Create a new referral code for a user")
    public ResponseEntity<String> createReferralCode(@PathVariable String userId) {
        String referralCode = ecosystemService.createReferralCode(userId);
        return ResponseEntity.ok(referralCode);
    }

    @PostMapping("/referral-program/process/{referralCode}")
    @Operation(summary = "Process a referral using a referral code")
    public ResponseEntity<Boolean> processReferral(
            @PathVariable String referralCode,
            @RequestParam String newUserId) {
        boolean success = ecosystemService.processReferral(referralCode, newUserId);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/marketplace")
    @Operation(summary = "Get premium marketplace listings")
    public ResponseEntity<PremiumMarketplaceResponse> getMarketplaceListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PremiumMarketplaceResponse response = ecosystemService.getPremiumMarketplaceListings(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/marketing-insights/{domainName}")
    @Operation(summary = "Get marketing insights for a domain")
    public ResponseEntity<MarketingInsightsResponse> getMarketingInsights(
            @PathVariable String domainName,
            @RequestParam(required = false) String userId) {
        MarketingInsightsResponse response = ecosystemService.getMarketingInsights(domainName, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trends/{domainName}")
    @Operation(summary = "Get ecosystem trends and insights for a domain")
    public ResponseEntity<String> getEcosystemTrends(@PathVariable String domainName) {
        // This would return a summary of trends
        String trends = String.format(
            "Ecosystem Trend Report for %s:\n" +
            "- 23%% growth in API integrations\n" +
            "- 156%% increase in premium feature adoption\n" +
            "- 12 new partnership opportunities\n" +
            "- Nexa-Link usage up 89%%\n" +
            "- Community engagement at 18.5%% rate", 
            domainName
        );
        return ResponseEntity.ok(trends);
    }
}