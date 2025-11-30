package com.myproject.nexa.repositories;

import com.myproject.nexa.dto.projection.UserProjection;
import com.myproject.nexa.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles r WHERE r.name = :roleName")
    java.util.List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsernameWithRoles(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmailWithRoles(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true AND u.deletedAt IS NULL")
    long countEnabledUsers();

    // Projection queries for performance optimization
    @Query("SELECT u.id as id, u.username as username, u.email as email, u.firstName as firstName, " +
           "u.lastName as lastName, u.phone as phone, u.address as address, " +
           "u.enabled as enabled, u.createdAt as createdAt, u.updatedAt as updatedAt " +
           "FROM User u WHERE u.deletedAt IS NULL")
    Page<UserProjection> findAllProjectedBy(Pageable pageable);

    @Query("SELECT u.id as id, u.username as username, u.email as email, u.firstName as firstName, " +
           "u.lastName as lastName, u.phone as phone, u.address as address, " +
           "u.enabled as enabled, u.createdAt as createdAt, u.updatedAt as updatedAt " +
           "FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<UserProjection> findProjectedById(@Param("id") Long id);

    @Query("SELECT u.id as id, u.username as username, u.email as email, u.firstName as firstName, " +
           "u.lastName as lastName, u.phone as phone, u.address as address, " +
           "u.enabled as enabled, u.createdAt as createdAt, u.updatedAt as updatedAt " +
           "FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<UserProjection> findProjectedByUsername(@Param("username") String username);

    @Query("SELECT u.id as id, u.username as username, u.email as email, u.firstName as firstName, " +
           "u.lastName as lastName, u.phone as phone, u.address as address, " +
           "u.enabled as enabled, u.createdAt as createdAt, u.updatedAt as updatedAt " +
           "FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<UserProjection> findProjectedByEmail(@Param("email") String email);
}