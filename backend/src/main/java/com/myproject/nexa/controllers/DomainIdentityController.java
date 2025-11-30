package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.DomainAvailabilityResponse;
import com.myproject.nexa.dto.response.DomainIdentityResponse;
import com.myproject.nexa.services.DomainIdentityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domain")
@RequiredArgsConstructor
@Tag(name = "Domain Identity", description = "APIs for domain identity features leveraging .nexa advantages")
public class DomainIdentityController {

    private final DomainIdentityService domainIdentityService;

    @GetMapping("/availability/{domainName}")
    @Operation(summary = "Check domain availability in .nexa namespace")
    public ResponseEntity<DomainAvailabilityResponse> checkDomainAvailability(@PathVariable String domainName) {
        DomainAvailabilityResponse response = domainIdentityService.checkDomainAvailability(domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/identity/{domainName}")
    @Operation(summary = "Get domain identity information")
    public ResponseEntity<DomainIdentityResponse> getDomainIdentity(@PathVariable String domainName) {
        DomainIdentityResponse response = domainIdentityService.getDomainIdentityInfo(domainName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/premium-domains")
    @Operation(summary = "Get available premium domain names")
    public ResponseEntity<List<DomainAvailabilityResponse>> getAvailablePremiumDomains(
            @RequestParam(defaultValue = "10") int count) {
        List<DomainAvailabilityResponse> response = domainIdentityService.getAvailablePremiumDomains(count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tld-comparison")
    @Operation(summary = "Get TLD comparison for a specific use case")
    public ResponseEntity<String> getTldComparison(
            @RequestParam String useCase,
            @RequestParam(required = false) String domainName) {
        String comparison = domainIdentityService.getTldComparisonForUseCase(useCase, domainName);
        return ResponseEntity.ok(comparison);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get domain recommendations based on business name and use case")
    public ResponseEntity<List<String>> getDomainRecommendations(
            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) String useCase,
            @RequestParam(defaultValue = "5") int count) {
        List<String> recommendations = domainIdentityService.getDomainRecommendations(businessName, useCase, count);
        return ResponseEntity.ok(recommendations);
    }
}