package com.myproject.nexa.services;

/**
 * Base service interface providing common enterprise service patterns
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <R> Response DTO type
 */
public interface BaseService<T, ID, R> {
    
    /**
     * Find entity by ID with transaction boundary
     */
    R findById(ID id);
    
    /**
     * Save entity with transaction boundary
     */
    R save(T entity);
    
    /**
     * Update entity with transaction boundary
     */
    R update(ID id, T entity);
    
    /**
     * Delete entity with transaction boundary
     */
    void delete(ID id);
    
    /**
     * Check if entity exists
     */
    boolean exists(ID id);
}