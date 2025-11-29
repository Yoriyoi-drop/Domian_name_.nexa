package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for database query optimization
 */
@Component
@Slf4j
public class DatabaseOptimizationUtil {

    /**
     * Batch process a large collection to avoid memory issues
     */
    public static <T, R> List<R> batchProcess(Collection<T> items,
                                              int batchSize,
                                              Function<List<T>, List<R>> processor) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        List<T> itemList = List.copyOf(items);

        return IntStream.range(0, (int) Math.ceil((double) itemList.size() / batchSize))
                .mapToObj(i -> itemList.subList(
                        i * batchSize,
                        Math.min((i + 1) * batchSize, itemList.size())
                ))
                .map(processor)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Optimize N+1 query problem by fetching related entities in batches
     */
    public static <T, R> List<R> fetchInBatches(List<T> entities, 
                                                int batchSize,
                                                Function<List<T>, List<R>> fetcher) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return batchProcess(entities, batchSize, fetcher);
    }

    /**
     * Create optimized IN query parameter list to avoid SQL injection and performance issues
     */
    public static <T> List<T> validateAndLimitInClause(List<T> values, int maxLimit) {
        if (values == null) {
            return List.of();
        }

        if (values.size() > maxLimit) {
            log.warn("Large IN clause detected: {} items, limiting to {}", values.size(), maxLimit);
            return values.stream().limit(maxLimit).collect(Collectors.toList());
        }

        return values;
    }

    /**
     * Check if a query should use pagination to avoid memory issues
     */
    public static boolean shouldPaginate(long totalSize, int threshold) {
        return totalSize > threshold;
    }

    /**
     * Optimize a query by limiting result size
     */
    public static <T> List<T> limitResults(List<T> results, int maxResults) {
        if (results == null) {
            return List.of();
        }

        if (results.size() <= maxResults) {
            return results;
        }

        log.warn("Query result size {} exceeds limit {}, truncating to {}", 
                results.size(), maxResults, maxResults);
        return results.stream().limit(maxResults).collect(Collectors.toList());
    }
}