package com.myproject.nexa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Base repository interface with optimized query methods
 * @param <T> Entity type
 * @param <ID> ID type
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Find all entities with dynamic filtering and pagination
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable);

    /**
     * Find all entities with dynamic filtering
     */
    List<T> findAll(Specification<T> spec);

    /**
     * Find entities by multiple IDs with optimized batch loading
     */
    List<T> findByIdIn(List<ID> ids);

    /**
     * Check existence for non-deleted entities
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.deletedAt IS NULL")
    boolean existsByIdAndDeletedAtIsNull(@Param("id") ID id);

    /**
     * Count non-deleted entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    long countByDeletedAtIsNull();

    /**
     * Soft delete entity
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = :deletedAt WHERE e.id = :id")
    void softDeleteById(@Param("id") ID id, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Find all active (non-deleted) entities with pagination
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    Page<T> findAllActive(Pageable pageable);

    /**
     * Find all active (non-deleted) entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    List<T> findAllActive();
}