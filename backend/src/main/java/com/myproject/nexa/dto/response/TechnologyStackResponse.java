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
public class TechnologyStackResponse {
    private String architecture;
    private String backendFramework;
    private String frontendFramework;
    private String database;
    private String caching;
    private String messaging;
    private String monitoring;
    private String security;
    private String deployment;
    private List<String> modernPractices;
}