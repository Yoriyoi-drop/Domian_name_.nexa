package com.myproject.nexa.services;

import com.myproject.nexa.dto.response.UniversalScopeResponse;
import com.myproject.nexa.dto.response.SubdomainConfigurationResponse;
import com.myproject.nexa.dto.response.NexaLinkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Universal Scope features that demonstrate how .nexa works
 * for any business aspect, unlike niche TLDs like .app, .dev, etc.
 */
@Service
@Slf4j
public class UniversalScopeService {

    /**
     * Get universal scope information for a domain
     */
    public UniversalScopeResponse getUniversalScopeInfo(String domainName) {
        log.info("Getting universal scope info for: {}", domainName);
        
        return UniversalScopeResponse.builder()
                .domain(domainName)
                .scopeCategories(getScopeCategories())
                .subdomainExamples(getSubdomainExamples(domainName))
                .benefits(getUniversalBenefits())
                .comparisonWithNicheTlds(getComparisonWithNicheTlds())
                .recommendedSubdomains(getRecommendedSubdomains(domainName))
                .build();
    }

    /**
     * Configure a subdomain for a specific purpose
     */
    public SubdomainConfigurationResponse configureSubdomain(String domainName, String subdomain, String purpose) {
        log.info("Configuring subdomain {} for domain {} with purpose: {}", subdomain, domainName, purpose);
        
        // Validate subdomain
        if (!isValidSubdomain(subdomain)) {
            throw new IllegalArgumentException("Invalid subdomain format: " + subdomain);
        }

        String fullSubdomain = subdomain + "." + domainName;
        
        return SubdomainConfigurationResponse.builder()
                .subdomain(fullSubdomain)
                .purpose(purpose)
                .configuration(getSubdomainConfiguration(purpose))
                .recommendedSettings(getRecommendedSettings(purpose))
                .benefits(getBenefitsForPurpose(purpose))
                .build();
    }

    /**
     * Generate a Nexa-Link short URL
     */
    public NexaLinkResponse generateNexaLink(String longUrl, String customAlias) {
        log.info("Generating Nexa-Link for URL: {}", longUrl);
        
        String shortCode = customAlias != null ? customAlias : generateShortCode();
        
        return NexaLinkResponse.builder()
                .originalUrl(longUrl)
                .shortUrl("go.nexa/" + shortCode)
                .clicks(0)
                .createdAt(new Date())
                .expiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // 30 days
                .build();
    }

    /**
     * Validate if a subdomain is suitable for universal scope
     */
    public boolean isValidForUniversalScope(String subdomain) {
        // Check against common service names that work well with .nexa
        List<String> validSubdomains = Arrays.asList(
            "www", "api", "app", "admin", "dashboard", "docs", "status", 
            "blog", "shop", "portal", "auth", "cdn", "edge", "data",
            "services", "cloud", "ai", "dev", "staging", "prod", "qa"
        );

        return validSubdomains.contains(subdomain.toLowerCase());
    }

    /**
     * Get scope categories that .nexa supports universally
     */
    private List<String> getScopeCategories() {
        return Arrays.asList(
            "Marketing & Branding",
            "Product & Application",
            "Developer & API",
            "Documentation",
            "Analytics & Monitoring",
            "E-commerce",
            "Content Management",
            "Enterprise Services"
        );
    }

    /**
     * Get examples of subdomains that work naturally with .nexa
     */
    private Map<String, String> getSubdomainExamples(String domainName) {
        return Map.of(
            "www." + domainName, "Corporate/Markeing presence",
            "app." + domainName, "Product application",
            "api." + domainName, "Developer APIs",
            "docs." + domainName, "Documentation",
            "status." + domainName, "System status page",
            "blog." + domainName, "Content/Marketing blog",
            "shop." + domainName, "E-commerce platform",
            "admin." + domainName, "Admin panel",
            "auth." + domainName, "Authentication service",
            "cdn." + domainName, "Content delivery network"
        );
    }

    /**
     * Get benefits of universal scope
     */
    private List<String> getUniversalBenefits() {
        return Arrays.asList(
            "Works for any business aspect without implying specific purpose",
            "Professional appearance for marketing, product, and developer contexts",
            "Easy to remember and brand-consistent",
            "No technical limitations or implied restrictions",
            "Scalable to any number of subdomains",
            "Flexible for future business needs"
        );
    }

    /**
     * Compare with niche TLDs
     */
    private Map<String, Map<String, String>> getComparisonWithNicheTlds() {
        Map<String, Map<String, String>> comparison = new HashMap<>();
        
        Map<String, String> nexa = new HashMap<>();
        nexa.put("Scope", "Universal - works for any purpose");
        nexa.put("Perception", "Modern, tech-forward");
        nexa.put("Flexibility", "Complete - any subdomain works naturally");
        nexa.put("Limitation", "None - truly universal");
        comparison.put(".nexa", nexa);
        
        Map<String, String> app = new HashMap<>();
        app.put("Scope", "Specific - implies application only");
        app.put("Perception", "App-focused");
        app.put("Flexibility", "Limited - marketing/dashboards look strange");
        app.put("Limitation", "Implies single-purpose platform");
        comparison.put(".app", app);
        
        Map<String, String> dev = new HashMap<>();
        dev.put("Scope", "Specific - developer-focused");
        dev.put("Perception", "Developer tools only");
        dev.put("Flexibility", "Limited - client-facing sites look inappropriate");
        dev.put("Limitation", "Not suitable for marketing/branding");
        comparison.put(".dev", dev);
        
        Map<String, String> io = new HashMap<>();
        io.put("Scope", "Broad but associated with tech");
        io.put("Perception", "Tech startup");
        io.put("Flexibility", "Good but has territorial/branding issues");
        io.put("Limitation", "Geopolitical risk");
        comparison.put(".io", io);

        return comparison;
    }

    /**
     * Get recommended subdomains for different business needs
     */
    private List<String> getRecommendedSubdomains(String domainName) {
        return Arrays.asList(
            "www." + domainName + " - Main website",
            "app." + domainName + " - Application interface",
            "api." + domainName + " - API endpoints",
            "docs." + domainName + " - Documentation",
            "status." + domainName + " - Status monitoring",
            "admin." + domainName + " - Administration panel",
            "cdn." + domainName + " - Content delivery",
            "staging." + domainName + " - Staging environment"
        );
    }

    /**
     * Get configuration for a specific purpose
     */
    private Map<String, Object> getSubdomainConfiguration(String purpose) {
        Map<String, Object> config = new HashMap<>();
        
        switch (purpose.toLowerCase()) {
            case "api":
                config.put("recommended_ssl", true);
                config.put("rate_limiting", true);
                config.put("authentication", true);
                config.put("monitoring", "API analytics and monitoring");
                break;
            case "cms":
            case "blog":
                config.put("recommended_ssl", true);
                config.put("caching", "Aggressive page caching");
                config.put("cdn", true);
                config.put("monitoring", "Content performance monitoring");
                break;
            case "app":
                config.put("recommended_ssl", true);
                config.put("websocket", true);
                config.put("real_time_updates", true);
                config.put("monitoring", "Application performance monitoring");
                break;
            case "marketing":
                config.put("recommended_ssl", true);
                config.put("seo_optimized", true);
                config.put("analytics", "Visitor analytics and conversion tracking");
                config.put("ab_testing", true);
                break;
            case "admin":
                config.put("recommended_ssl", true);
                config.put("security", "Enhanced security and audit logging");
                config.put("restrictions", "IP whitelisting possible");
                config.put("monitoring", "Security and access monitoring");
                break;
            default:
                config.put("recommended_ssl", true);
                config.put("basic_setup", "Standard configuration for " + purpose);
                config.put("monitoring", "General performance monitoring");
        }
        
        return config;
    }

    /**
     * Get recommended settings for specific purpose
     */
    private List<String> getRecommendedSettings(String purpose) {
        switch (purpose.toLowerCase()) {
            case "api":
                return Arrays.asList(
                    "Enable request/response logging",
                    "Set up rate limiting",
                    "Implement proper error handling",
                    "Enable CORS for allowed origins",
                    "Set up API versioning strategy"
                );
            case "app":
                return Arrays.asList(
                    "Enable WebSocket support",
                    "Set up real-time updates",
                    "Configure caching strategies",
                    "Set up performance monitoring",
                    "Implement user session management"
                );
            case "marketing":
                return Arrays.asList(
                    "Configure SEO meta tags",
                    "Set up Google Analytics",
                    "Enable page speed optimization",
                    "Configure social media previews",
                    "Set up conversion tracking"
                );
            default:
                return Arrays.asList(
                    "Enable HTTPS",
                    "Set up basic monitoring",
                    "Configure appropriate caching",
                    "Implement security headers"
                );
        }
    }

    /**
     * Get benefits for specific purpose
     */
    private List<String> getBenefitsForPurpose(String purpose) {
        switch (purpose.toLowerCase()) {
            case "api":
                return Arrays.asList(
                    "Professional API domain",
                    "Clear separation from frontend",
                    "Easy to version and manage",
                    "Better security posture"
                );
            case "app":
                return Arrays.asList(
                    "Intuitive application domain",
                    "Clear user expectation of interactive features",
                    "Good SEO for application content",
                    "Easy to remember for users"
                );
            case "marketing":
                return Arrays.asList(
                    "Professional brand presence",
                    "High SEO potential",
                    "Brand consistency",
                    "Trust and credibility"
                );
            default:
                return Arrays.asList(
                    "Professional appearance",
                    "Clear purpose indication",
                    "Easy to remember and share",
                    "Scalable for growth"
                );
        }
    }

    /**
     * Validate subdomain format
     */
    private boolean isValidSubdomain(String subdomain) {
        if (subdomain == null || subdomain.trim().isEmpty()) {
            return false;
        }
        // Basic validation: alphanumeric and hyphens, not starting/ending with hyphen
        return subdomain.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$");
    }

    /**
     * Generate a short code for Nexa-Link
     */
    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}