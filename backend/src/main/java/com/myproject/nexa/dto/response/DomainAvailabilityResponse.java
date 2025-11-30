package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainAvailabilityResponse {
    private String domain;
    private boolean available;
    private String reason;
    private List<String> suggestedAlternatives;
    private int premiumRanking; // 1-100 scale
}