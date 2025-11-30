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
public class SubdomainConfigurationResponse {
    private String subdomain;
    private String purpose;
    private Map<String, Object> configuration;
    private List<String> recommendedSettings;
    private List<String> benefits;
}