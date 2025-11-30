package com.myproject.nexa.controllers;

import com.myproject.nexa.dto.response.NexaLinkResponse;
import com.myproject.nexa.dto.response.SubdomainConfigurationResponse;
import com.myproject.nexa.dto.response.UniversalScopeResponse;
import com.myproject.nexa.services.UniversalScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/universal-scope")
@RequiredArgsConstructor
@Tag(name = "Universal Scope", description = "APIs for universal scope features addressing .app/.dev/.tech limitations")
public class UniversalScopeController {

    private final UniversalScopeService universalScopeService;

    @GetMapping("/info/{domainName}")
    @Operation(summary = "Get universal scope information for a domain")
    public ResponseEntity<UniversalScopeResponse> getUniversalScopeInfo(@PathVariable String domainName) {
        UniversalScopeResponse response = universalScopeService.getUniversalScopeInfo(domainName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/configure-subdomain")
    @Operation(summary = "Configure a subdomain for a specific purpose")
    public ResponseEntity<SubdomainConfigurationResponse> configureSubdomain(
            @RequestParam String domainName,
            @RequestParam String subdomain,
            @RequestParam String purpose) {
        SubdomainConfigurationResponse response = universalScopeService.configureSubdomain(domainName, subdomain, purpose);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nexa-link")
    @Operation(summary = "Generate a Nexa-Link short URL")
    public ResponseEntity<NexaLinkResponse> generateNexaLink(
            @RequestParam String longUrl,
            @RequestParam(required = false) String customAlias) {
        NexaLinkResponse response = universalScopeService.generateNexaLink(longUrl, customAlias);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-scope/{subdomain}")
    @Operation(summary = "Validate if a subdomain is suitable for universal scope")
    public ResponseEntity<Boolean> isValidForUniversalScope(@PathVariable String subdomain) {
        boolean valid = universalScopeService.isValidForUniversalScope(subdomain);
        return ResponseEntity.ok(valid);
    }

    @GetMapping("/recommended-subdomains/{domainName}")
    @Operation(summary = "Get recommended subdomains for a domain")
    public ResponseEntity<List<String>> getRecommendedSubdomains(@PathVariable String domainName) {
        List<String> subdomains = universalScopeService.getRecommendedSubdomains(domainName);
        return ResponseEntity.ok(subdomains);
    }
}