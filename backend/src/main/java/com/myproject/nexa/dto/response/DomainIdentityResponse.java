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
public class DomainIdentityResponse {
    private String domain;
    private List<String> identityBenefits;
    private List<String> strategicAdvantages;
    private Map<String, String> competitorComparison;
}