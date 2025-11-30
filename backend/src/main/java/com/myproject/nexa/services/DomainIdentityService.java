package com.myproject.nexa.services;

import com.myproject.nexa.dto.response.DomainAvailabilityResponse;
import com.myproject.nexa.dto.response.DomainIdentityResponse;

import java.util.List;

/**
 * Service interface for domain identity features that leverage the strategic
 * advantages of the .nexa domain extension over competing TLDs.
 */
public interface DomainIdentityService {
    
    /**
     * Check availability of a domain name in the .nexa namespace
     */
    DomainAvailabilityResponse checkDomainAvailability(String domainName);
    
    /**
     * Get domain identity information including strategic advantages
     */
    DomainIdentityResponse getDomainIdentityInfo(String domainName);
    
    /**
     * Get list of available premium domain names
     */
    List<DomainAvailabilityResponse> getAvailablePremiumDomains(int count);
    
    /**
     * Get comparison between .nexa and other TLDs for a given use case
     */
    String getTldComparisonForUseCase(String useCase, String domainName);
    
    /**
     * Generate domain recommendation based on business requirements
     */
    List<String> getDomainRecommendations(String businessName, String useCase, int count);
}