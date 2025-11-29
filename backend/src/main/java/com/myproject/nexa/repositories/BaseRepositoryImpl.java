package com.myproject.nexa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Base repository implementation with optimized query methods
 */
public class BaseRepositoryImpl<T, ID extends Serializable>
    extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        // Custom implementation with potential optimizations
        return super.findAll(spec, pageable);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        // Custom implementation with potential optimizations
        return super.findAll(spec);
    }

    @Override
    public List<T> findByIdIn(List<ID> ids) {
        // Using JPQL to optimize the query
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("SELECT e FROM %s e WHERE e.id IN :ids AND e.deletedAt IS NULL", entityName);

        return entityManager.createQuery(jpql, getDomainClass())
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public boolean existsByIdAndDeletedAtIsNull(ID id) {
        if (id == null) {
            return false;
        }

        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("SELECT COUNT(e) FROM %s e WHERE e.id = :id AND e.deletedAt IS NULL", entityName);

        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public long countByDeletedAtIsNull() {
        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("SELECT COUNT(e) FROM %s e WHERE e.deletedAt IS NULL", entityName);

        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public void softDeleteById(ID id, LocalDateTime deletedAt) {
        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("UPDATE %s e SET e.deletedAt = :deletedAt WHERE e.id = :id", entityName);

        entityManager.createQuery(jpql)
                .setParameter("id", id)
                .setParameter("deletedAt", deletedAt)
                .executeUpdate();
    }

    @Override
    public Page<T> findAllActive(Pageable pageable) {
        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("SELECT e FROM %s e WHERE e.deletedAt IS NULL", entityName);

        // Get total count
        String countJpql = String.format("SELECT COUNT(e) FROM %s e WHERE e.deletedAt IS NULL", entityName);
        long totalCount = entityManager.createQuery(countJpql, Long.class).getSingleResult();

        // Get page content
        List<T> content = entityManager.createQuery(jpql, getDomainClass())
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<T> findAllActive() {
        String entityName = getDomainClass().getSimpleName();
        String jpql = String.format("SELECT e FROM %s e WHERE e.deletedAt IS NULL", entityName);

        return entityManager.createQuery(jpql, getDomainClass()).getResultList();
    }
}