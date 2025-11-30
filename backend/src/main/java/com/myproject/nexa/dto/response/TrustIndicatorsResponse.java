package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustIndicatorsResponse {
    private String domain;
    private Double uptimePercentage;
    private Integer securityScore;
    private String complianceStatus;
    private List<String> dataCenterLocations;
    private List<String> certifications;
    private LocalDateTime lastAuditDate;
    private LocalDateTime nextAuditDate;
}