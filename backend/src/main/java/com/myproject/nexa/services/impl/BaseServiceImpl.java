package com.myproject.nexa.services.impl;

import com.myproject.nexa.entities.BaseEntity;
import com.myproject.nexa.services.BaseService;
import com.myproject.nexa.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Abstract base service implementation providing common enterprise service patterns
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <R> Response DTO type
 * @param <Repo> Repository type
 */
@Slf4j
public abstract class BaseServiceImpl<T extends BaseEntity, ID, R, Repo extends JpaRepository<T, ID>> implements BaseService<T, ID, R> {

    protected final Repo repository;
    
    public BaseServiceImpl(Repo repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public R findById(ID id) {
        log.debug("Finding entity with id: {}", id);
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    getEntityName() + " not found with id: " + id));

        // Check if entity is soft deleted
        if (entity.getDeletedAt() != null) {
            throw new ResourceNotFoundException(getEntityName() + " not found with id: " + id);
        }

        return mapToResponse(entity);
    }
    
    @Override
    @Transactional
    public R save(T entity) {
        log.debug("Saving entity: {}", entity);
        T savedEntity = repository.save(entity);
        return mapToResponse(savedEntity);
    }
    
    @Override
    @Transactional
    public R update(ID id, T entity) {
        log.debug("Updating entity with id: {}", id);
        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    getEntityName() + " not found with id: " + id));
        
        updateEntity(existingEntity, entity);
        T updatedEntity = repository.save(existingEntity);
        return mapToResponse(updatedEntity);
    }
    
    @Override
    @Transactional
    public void delete(ID id) {
        log.debug("Deleting entity with id: {}", id);
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    getEntityName() + " not found with id: " + id));
        repository.delete(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return repository.existsById(id);
    }
    
    /**
     * Map entity to response DTO
     */
    protected abstract R mapToResponse(T entity);
    
    /**
     * Update existing entity with values from new entity
     */
    protected abstract void updateEntity(T existing, T updated);
    
    /**
     * Get entity name for error messages
     */
    protected abstract String getEntityName();
    
    /**
     * Find entity by ID without throwing exception
     */
    @Transactional(readOnly = true)
    protected Optional<T> findByIdOptional(ID id) {
        return repository.findById(id);
    }
}