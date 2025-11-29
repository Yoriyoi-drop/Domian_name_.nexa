package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Utility for handling concurrent operations safely
 */
@Component
@Slf4j
public class ConcurrencyUtil {

    private final Executor executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );

    /**
     * Execute a task asynchronously with error handling
     */
    public <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Async task failed", throwable);
                }
            });
    }

    /**
     * Execute multiple tasks in parallel and wait for all to complete
     */
    @SafeVarargs
    public final <T> CompletableFuture<T[]> executeAll(Supplier<T>... tasks) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] futures = new CompletableFuture[tasks.length];
        
        for (int i = 0; i < tasks.length; i++) {
            futures[i] = executeAsync(tasks[i]);
        }
        
        return CompletableFuture.allOf(futures)
            .thenApply(v -> {
                T[] results = (T[]) new Object[futures.length];
                for (int i = 0; i < futures.length; i++) {
                    try {
                        results[i] = futures[i].join();
                    } catch (Exception e) {
                        log.error("Task failed", e);
                        results[i] = null;
                    }
                }
                return results;
            });
    }

    /**
     * Execute multiple tasks in parallel and return the first successful result
     */
    @SafeVarargs
    public final <T> CompletableFuture<T> executeAny(Supplier<T>... tasks) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] futures = new CompletableFuture[tasks.length];
        
        for (int i = 0; i < tasks.length; i++) {
            futures[i] = executeAsync(tasks[i]);
        }
        
        return CompletableFuture.anyOf(futures)
            .thenApply(result -> (T) result);
    }
}