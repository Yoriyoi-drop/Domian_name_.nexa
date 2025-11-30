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
public class PerformanceOptimizationResponse {
    private String domain;
    private Long totalUsers;
    private Long activeUsers;
    private List<String> optimizationRecommendations;
    private Map<String, Object> performanceMetrics;
    private Map<String, Object> cachingStatus;
    private Map<String, Object> databaseOptimization;
    private Map<String, Object> asyncProcessing;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginatedUsersResponse {
        private List<?> content;
        private Long totalElements;
        private Integer totalPages;
        private Integer currentPage;
        private Integer pageSize;
        private Boolean isFirst;
        private Boolean isLast;
    }
}