package com.myproject.nexa.config;

import com.myproject.nexa.config.properties.AppProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Database configuration with optimized settings for performance
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSourceConfig {

    private final AppProperties appProperties;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        log.info("Initializing database connection pool with max size: {}",
                appProperties.getDatabase().getMaxPoolSize());

        HikariConfig config = new HikariConfig();

        // Database connection
        config.setJdbcUrl(System.getenv("DB_URL") != null ? System.getenv("DB_URL") :
                  System.getProperty("DB_URL", "jdbc:postgresql://localhost:5432/myproject_nexa"));
        config.setUsername(System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") :
                   System.getProperty("DB_USERNAME", "postgres"));
        config.setPassword(System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") :
                   System.getProperty("DB_PASSWORD", "postgres"));
        config.setDriverClassName("org.postgresql.Driver");

        // Connection pool configuration from app properties
        config.setMaximumPoolSize(appProperties.getDatabase().getMaxPoolSize());
        config.setConnectionTimeout(appProperties.getDatabase().getConnectionTimeout());
        config.setIdleTimeout(appProperties.getDatabase().getIdleTimeout());
        config.setMaxLifetime(appProperties.getDatabase().getMaxLifetime());

        // Additional performance optimizations
        config.setLeakDetectionThreshold(60000); // 60 seconds
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000);
        config.setInitializationFailTimeout(-1); // Continue even if initialization fails

        // Performance settings
        config.setPoolName("MyProjectNexa-HikariCP");

        // Enable prepared statement caching
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("Database connection pool initialized successfully");

        return dataSource;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        log.info("Transaction manager configured");
        return transactionManager;
    }
}