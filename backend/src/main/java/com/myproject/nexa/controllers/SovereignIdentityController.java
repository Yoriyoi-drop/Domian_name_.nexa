package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.SovereignIdentityResponse;
import com.myproject.nexa.dto.response.TrustIndicatorsResponse;
import com.myproject.nexa.services.SovereignIdentityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sovereign")
@RequiredArgsConstructor
@Tag(name = "Sovereign Identity", description = "APIs for geopolitical risk assessment and trust indicators")
public class SovereignIdentityController {

    private final SovereignIdentityService sovereignIdentityService;

    @GetMapping("/identity/{domainName}")
    @Operation(summary = "Get sovereign identity information for a domain")
    public ResponseEntity<SovereignIdentityResponse> getSovereignIdentity(
            @PathVariable String domainName) {
        SovereignIdentityResponse response = sovereignIdentityService.getSovereignIdentityInfo(domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trust/{domainName}")
    @Operation(summary = "Get trust indicators for a domain")
    public ResponseEntity<TrustIndicatorsResponse> getTrustIndicators(
            @PathVariable String domainName) {
        TrustIndicatorsResponse response = sovereignIdentityService.getTrustIndicators(domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/geopolitical-risk-report")
    @Operation(summary = "Get geopolitical risk report comparing TLDs")
    public ResponseEntity<Map<String, String>> getGeopoliticalRiskReport() {
        Map<String, String> report = sovereignIdentityService.getGeopoliticalRiskReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/risk-mitigation/{domainName}")
    @Operation(summary = "Get risk mitigation recommendations for a domain")
    public ResponseEntity<List<String>> getRiskMitigationRecommendations(
            @PathVariable String domainName) {
        List<String> recommendations = sovereignIdentityService.getRiskMitigationRecommendations(domainName);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/is-safe/{domainName}")
    @Operation(summary = "Check if a domain is geopolitically safe")
    public ResponseEntity<Boolean> isDomainGeopoliticallySafe(
            @PathVariable String domainName) {
        boolean isSafe = sovereignIdentityService.isDomainGeopoliticallySafe(domainName);
        return ResponseEntity.ok(isSafe);
    }
}