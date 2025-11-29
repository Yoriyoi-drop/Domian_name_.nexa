package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Database performance monitoring utility
 */
@Component
@Slf4j
public class DatabasePerformanceMonitor {

    private final ConcurrentHashMap<String, AtomicLong> queryCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> totalExecutionTime = new ConcurrentHashMap<>();
    private final AtomicLong totalQueries = new AtomicLong(0);
    private final long SLOW_QUERY_THRESHOLD_MS = 100; // Queries taking longer than this are considered slow

    /**
     * Record a query execution
     */
    public void recordQuery(String queryName, long executionTimeMs) {
        totalQueries.incrementAndGet();
        
        queryCount.computeIfAbsent(queryName, k -> new AtomicLong(0)).incrementAndGet();
        totalExecutionTime.computeIfAbsent(queryName, k -> new AtomicLong(0)).addAndGet(executionTimeMs);
        
        if (executionTimeMs > SLOW_QUERY_THRESHOLD_MS) {
            log.warn("Slow query detected: {} took {}ms", queryName, executionTimeMs);
        }
    }

    /**
     * Get average execution time for a query
     */
    public double getAverageExecutionTime(String queryName) {
        AtomicLong count = queryCount.get(queryName);
        AtomicLong totalTime = totalExecutionTime.get(queryName);
        
        if (count == null || totalTime == null || count.get() == 0) {
            return 0.0;
        }
        
        return (double) totalTime.get() / count.get();
    }

    /**
     * Get total execution time for a query
     */
    public long getTotalExecutionTime(String queryName) {
        AtomicLong totalTime = totalExecutionTime.get(queryName);
        return totalTime != null ? totalTime.get() : 0;
    }

    /**
     * Get execution count for a query
     */
    public long getExecutionCount(String queryName) {
        AtomicLong count = queryCount.get(queryName);
        return count != null ? count.get() : 0;
    }

    /**
     * Get total number of queries executed
     */
    public long getTotalQueries() {
        return totalQueries.get();
    }

    /**
     * Reset all statistics
     */
    public void reset() {
        queryCount.clear();
        totalExecutionTime.clear();
        totalQueries.set(0);
    }

    /**
     * Log performance summary
     */
    public void logPerformanceSummary() {
        log.info("Database Performance Summary:");
        log.info("Total Queries: {}", getTotalQueries());
        
        queryCount.forEach((queryName, count) -> {
            long execCount = count.get();
            long totalExecTime = getTotalExecutionTime(queryName);
            double avgTime = getAverageExecutionTime(queryName);
            
            log.info("Query: {}, Executions: {}, Total Time: {}ms, Avg Time: {}ms", 
                    queryName, execCount, totalExecTime, String.format("%.2f", avgTime));
        });
    }
}