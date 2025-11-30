package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.response.DomainAvailabilityResponse;
import com.myproject.nexa.dto.response.DomainIdentityResponse;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.DomainIdentityService;
import com.myproject.nexa.utils.AuditLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomainIdentityServiceImpl implements DomainIdentityService {

    private final UserRepository userRepository;
    private final AuditLogUtil auditLogUtil;
    private final Random random = new Random();

    @Override
    public DomainAvailabilityResponse checkDomainAvailability(String domainName) {
        log.info("Checking domain availability for: {}", domainName);

        // Basic validation
        if (domainName == null || domainName.trim().isEmpty()) {
            return DomainAvailabilityResponse.builder()
                    .domain(domainName)
                    .available(false)
                    .reason("Domain name cannot be empty")
                    .build();
        }

        // Clean domain name (remove .nexa if present)
        String cleanDomainName = domainName.toLowerCase().trim();
        if (cleanDomainName.endsWith(".nexa")) {
            cleanDomainName = cleanDomainName.substring(0, cleanDomainName.length() - 5);
        }

        // Validate domain format
        if (!isValidDomainName(cleanDomainName)) {
            return DomainAvailabilityResponse.builder()
                    .domain(domainName)
                    .available(false)
                    .reason("Invalid domain name format")
                    .build();
        }

        // Check if domain already exists in the system (we'll use users as proxy for
        // domain usage)
        boolean exists = userRepository.existsByUsername(cleanDomainName);

        DomainAvailabilityResponse response = DomainAvailabilityResponse.builder()
                .domain(cleanDomainName + ".nexa")
                .available(!exists)
                .suggestedAlternatives(generateSuggestions(cleanDomainName, 3))
                .premiumRanking(calculatePremiumRanking(cleanDomainName))
                .build();

        // Record audit log
        // Record audit log
        auditLogUtil.logSystemEvent(
                "DomainIdentity",
                "DOMAIN_AVAILABILITY_CHECK",
                Map.of("domain", cleanDomainName, "available", !exists));

        return response;
    }

    @Override
    public DomainIdentityResponse getDomainIdentityInfo(String domainName) {
        log.info("Getting domain identity info for: {}", domainName);

        String cleanDomainName = domainName.toLowerCase().trim();
        if (cleanDomainName.endsWith(".nexa")) {
            cleanDomainName = cleanDomainName.substring(0, cleanDomainName.length() - 5);
        }

        return DomainIdentityResponse.builder()
                .domain(cleanDomainName + ".nexa")
                .identityBenefits(getIdentityBenefits(cleanDomainName))
                .strategicAdvantages(getStrategicAdvantages())
                .competitorComparison(getCompetitorComparison())
                .build();
    }

    @Override
    public List<DomainAvailabilityResponse> getAvailablePremiumDomains(int count) {
        log.info("Getting {} available premium domains", count);

        List<String> premiumSuggestions = Arrays.asList(
                "cloud", "tech", "ai", "data", "next", "scale", "prime", "core",
                "flow", "pulse", "edge", "zen", "nova", "fuse", "link", "sync",
                "forge", "pulse", "grid", "wave", "beam", "node", "hub", "lab");

        List<DomainAvailabilityResponse> result = new ArrayList<>();

        for (int i = 0; i < count && i < premiumSuggestions.size(); i++) {
            String domain = premiumSuggestions.get(i);
            DomainAvailabilityResponse response = DomainAvailabilityResponse.builder()
                    .domain(domain + ".nexa")
                    .available(true) // Assuming these are available for demo purposes
                    .premiumRanking(calculatePremiumRanking(domain))
                    .build();
            result.add(response);
        }

        return result;
    }

    @Override
    public String getTldComparisonForUseCase(String useCase, String domainName) {
        log.info("Getting TLD comparison for use case: {} and domain: {}", useCase, domainName);

        StringBuilder comparison = new StringBuilder();
        comparison.append("TLD Comparison for '").append(useCase).append("':\n\n");

        comparison.append(".nexa Advantages:\n");
        comparison.append("- Premium namespace availability (short, memorable names still available)\n");
        comparison.append("- Sovereign identity (no geopolitical risks)\n");
        comparison.append("- Universal scope (works for any business aspect)\n");
        comparison.append("- Future-first perception (modern, innovative)\n");
        comparison.append("- Exclusive ecosystem features\n\n");

        comparison.append(".com Limitations:\n");
        comparison.append("- Name saturation (good names already taken)\n");
        comparison.append("- Legacy perception\n\n");

        comparison.append(".io/.ai Limitations:\n");
        comparison.append("- Geopolitical risks (ccTLD territory concerns)\n");
        comparison.append("- Cost considerations\n\n");

        comparison.append(".app/.dev/.tech Limitations:\n");
        comparison.append("- Niche restrictions (implied specific purpose)\n");
        comparison.append("- Less universal branding\n\n");

        return comparison.toString();
    }

    @Override
    public List<String> getDomainRecommendations(String businessName, String useCase, int count) {
        log.info("Getting {} domain recommendations for business: {} and use case: {}", count, businessName, useCase);

        List<String> recommendations = new ArrayList<>();

        // Generate recommendations based on business name
        if (businessName != null && !businessName.trim().isEmpty()) {
            recommendations.add(businessName + ".nexa");
            recommendations.add(businessName.toLowerCase().replace(" ", "") + ".nexa");
            recommendations.add(businessName.toLowerCase().replace(" ", "-") + ".nexa");
        }

        // Add generic recommendations based on use case
        if (useCase != null) {
            switch (useCase.toLowerCase()) {
                case "technology":
                case "tech":
                    recommendations.add("tech.nexa");
                    recommendations.add("digital.nexa");
                    recommendations.add("innovate.nexa");
                    break;
                case "business":
                case "enterprise":
                    recommendations.add("enterprise.nexa");
                    recommendations.add("business.nexa");
                    recommendations.add("scale.nexa");
                    break;
                case "gaming":
                    recommendations.add("game.nexa");
                    recommendations.add("play.nexa");
                    recommendations.add("quest.nexa");
                    break;
                case "finance":
                case "fintech":
                    recommendations.add("finance.nexa");
                    recommendations.add("fin.nexa");
                    recommendations.add("capital.nexa");
                    break;
                default:
                    recommendations.add("next.nexa");
                    recommendations.add("prime.nexa");
                    recommendations.add("core.nexa");
            }
        }

        // Limit to requested count
        return recommendations.stream()
                .limit(count)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isValidDomainName(String domainName) {
        // Domain name can contain letters, numbers, and hyphens, but not start or end
        // with hyphen
        return Pattern.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$", domainName)
                && domainName.length() >= 2
                && domainName.length() <= 63;
    }

    private List<String> generateSuggestions(String baseDomain, int count) {
        List<String> suggestions = new ArrayList<>();

        // Add common variations
        suggestions.add(baseDomain + "app.nexa");
        suggestions.add(baseDomain + "io.nexa");
        suggestions.add("get-" + baseDomain + ".nexa");
        suggestions.add("try-" + baseDomain + ".nexa");

        return suggestions.stream().limit(count).collect(Collectors.toList());
    }

    private int calculatePremiumRanking(String domainName) {
        // Simple algorithm: shorter, more vowel-consonant balanced names get higher
        // ranking
        int lengthScore = Math.max(0, 10 - domainName.length()); // Shorter is better (max 8 chars)
        int vowelCount = (int) domainName.chars()
                .filter(c -> "aeiou".indexOf(Character.toLowerCase(c)) >= 0)
                .count();
        int consonantCount = domainName.length() - vowelCount;

        // Balanced vowel/consonant ratio gets higher score
        double ratio = consonantCount > 0 ? (double) vowelCount / consonantCount : 0;
        int balanceScore = (int) (20 * Math.max(0, 1 - Math.abs(ratio - 0.6))); // Optimal ratio ~0.6

        // Avoid repetitive characters
        int repetitionPenalty = 0;
        for (int i = 1; i < domainName.length(); i++) {
            if (domainName.charAt(i) == domainName.charAt(i - 1)) {
                repetitionPenalty += 10;
            }
        }

        int score = Math.max(1, Math.min(100, lengthScore * 3 + balanceScore * 2 - repetitionPenalty));
        return score;
    }

    private List<String> getIdentityBenefits(String domainName) {
        return Arrays.asList(
                "Premium Namespace Availability: Short, memorable names still available unlike .com",
                "Sovereign Identity: No geopolitical risks unlike .io/.ai ccTLDs",
                "Universal Scope: Works for marketing, app, and developer aspects unlike niche TLDs",
                "Future-First Prestige: Modern, innovative perception",
                "Flexible Security: Enterprise-grade security with configuration flexibility",
                "Innovation Badge: Domain itself signals cutting-edge technology");
    }

    private List<String> getStrategicAdvantages() {
        return Arrays.asList(
                "Addresses .com saturation with premium namespace availability",
                "Eliminates .io/.ai geopolitical risks with sovereign identity",
                "Overcomes .app/.dev/.tech niche restrictions with universal scope",
                "Counters legacy domain perceptions with future-first positioning",
                "Creates unique ecosystem advantages competitors cannot replicate");
    }

    private Map<String, String> getCompetitorComparison() {
        return Map.of(
                ".com", "Saturation - good names unavailable",
                ".io", "Geopolitical risk - British Indian Ocean Territory",
                ".ai", "Geopolitical risk - Anguilla territory",
                ".app", "Niche restriction - implies app-only",
                ".dev", "Niche restriction - developer-focused only",
                ".net", "Legacy perception - fallback choice",
                ".nexa", "Premium availability, sovereign, universal, modern");
    }
}