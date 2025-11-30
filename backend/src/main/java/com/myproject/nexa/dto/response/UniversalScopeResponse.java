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
public class UniversalScopeResponse {
    private String domain;
    private List<String> scopeCategories;
    private Map<String, String> subdomainExamples;
    private List<String> benefits;
    private Map<String, Map<String, String>> comparisonWithNicheTlds;
    private List<String> recommendedSubdomains;
}