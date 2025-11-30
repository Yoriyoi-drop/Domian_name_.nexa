# Deployment and Troubleshooting Procedures

## Table of Contents
1. [Deployment Overview](#deployment-overview)
2. [Production Deployment](#production-deployment)
3. [Staging Deployment](#staging-deployment)
4. [Database Migration Procedures](#database-migration-procedures)
5. [Rollback Procedures](#rollback-procedures)
6. [Troubleshooting](#troubleshooting)
7. [Common Issues and Solutions](#common-issues-and-solutions)
8. [Monitoring and Health Checks](#monitoring-and-health-checks)

## Deployment Overview

MyProject.nexa follows a continuous deployment model with automated pipelines. All deployments are managed through GitHub Actions and Kubernetes.

### Deployment Architecture
- **Source Control**: GitHub with protected branches
- **CI/CD**: GitHub Actions
- **Container Registry**: AWS ECR
- **Orchestration**: Kubernetes (EKS)
- **Load Balancing**: AWS Application Load Balancer
- **Database**: AWS RDS PostgreSQL

### Deployment Strategies
- **Staging**: Rolling updates with blue-green approach
- **Production**: Blue-green deployment for zero-downtime releases

## Production Deployment

### Prerequisites
- Merge to `main` branch
- All CI tests passing
- Security scans completed successfully
- Database migration verification
- Staging environment validation

### Deployment Steps
1. **Code Review and Approval**
   - Code must be reviewed by at least 2 senior developers
   - All comments resolved
   - Approvals obtained

2. **Pre-deployment Verification**
   - Run integration tests
   - Verify security scan results
   - Confirm staging environment stability

3. **Trigger Production Deployment**
   - Push merge commit to `main` branch
   - GitHub Action automatically triggers:
     - Build new Docker images
     - Run security scans
     - Deploy using blue-green strategy
     - Run post-deployment tests

4. **Post-deployment Validation**
   - Verify application health
   - Check monitoring dashboards
   - Validate key user flows
   - Confirm no error spikes in logs

### Blue-Green Deployment Process
1. Deploy new version to "green" environment
2. Run health checks on green environment
3. Switch traffic from "blue" to "green"
4. Monitor for 5 minutes
5. If issues arise, switch traffic back to "blue"
6. If successful, scale down "blue" environment

## Staging Deployment

### Process
1. Push changes to `develop` branch
2. GitHub Action triggers:
   - Build Docker images
   - Deploy to staging EKS cluster
   - Run smoke tests
   - Update staging environment

### Staging Validation
- Manual testing of key features
- API validation
- Performance checks
- Security validation

## Database Migration Procedures

### Migration Development
1. Create migration script in `src/main/resources/db/migration`
2. Follow Flyway naming convention: `V[version]__[description].sql`
3. Include rollback script where possible: `U[version]__[description].sql`

### Migration Testing
1. Test migration on local database
2. Test migration on staging database
3. Verify data integrity after migration
4. Test application functionality with new schema

### Migration Deployment
1. Migrations run automatically during application startup
2. Monitor migration logs during deployment
3. Have rollback plan ready
4. Test application functionality after migration

### Rollback Plan
1. If migration fails, stop application deployment
2. Manually revert database changes if needed
3. Rollback application to previous version
4. Investigate and fix migration script
5. Retry deployment

## Rollback Procedures

### Automatic Rollback Triggers
- Health check failures after deployment
- High error rate (>5% of requests)
- Response time degradation (>3x normal)

### Manual Rollback Process
1. **Identify the Need for Rollback**
   - Monitor dashboards and alerts
   - Error logs and application metrics
   - User feedback and support tickets

2. **Execute Rollback**
   - Access the rollback workflow in GitHub Actions
   - Select the version to rollback to
   - Provide reason for rollback
   - Confirm rollback execution

3. **Post-Rollback Actions**
   - Verify system stability
   - Notify stakeholders
   - Document the incident
   - Plan for future prevention

### Rollback Commands (Manual)
```bash
# For Kubernetes deployments
kubectl rollout undo deployment/myproject-nexa-backend -n myproject-nexa

# For specific revision
kubectl rollout undo deployment/myproject-nexa-backend --to-revision=2 -n myproject-nexa
```

## Troubleshooting

### Application Issues

#### Service Not Starting
1. Check application logs: `kubectl logs -f deployment/myproject-nexa-backend -n myproject-nexa`
2. Verify environment variables and secrets
3. Check database connectivity
4. Review configuration files

#### High Memory Usage
1. Check for memory leaks in application logs
2. Review heap dump and thread dump
3. Optimize garbage collection settings
4. Increase memory limits if needed

#### High CPU Usage
1. Analyze application performance metrics
2. Identify slow queries and optimize them
3. Check for infinite loops or inefficient code
4. Scale up resources temporarily if needed

#### Database Connection Issues
1. Verify database availability and status
2. Check connection pool configuration
3. Review database logs for errors
4. Verify network connectivity

### Infrastructure Issues

#### Kubernetes
- **Pod CrashLoopBackOff**: Check application logs and resource limits
- **Service Unavailable**: Verify service configuration and endpoints
- **Node Issues**: Check node resource usage and status

#### Load Balancer
- **504 Gateway Timeout**: Check target group health and application response time
- **503 Service Unavailable**: Verify target registration and health checks

### Debugging Steps

1. **Information Gathering**
   - Application logs
   - System logs
   - Metrics and performance data
   - Error messages and stack traces

2. **Hypothesis Formation**
   - Identify affected components
   - Analyze recent changes
   - Review system dependencies

3. **Testing Hypotheses**
   - Reproduce issue in development/staging
   - Isolate problematic components
   - Verify fixes in safe environment

4. **Resolution and Validation**
   - Implement fix
   - Test thoroughly
   - Monitor for recurrence

## Common Issues and Solutions

### Issue: Application startup failure
**Symptoms**: Pod crashes during startup
**Causes**: 
- Missing environment variables
- Database connectivity issues
- Invalid configuration
- Missing dependencies
**Solutions**:
- Check environment configuration
- Verify database connectivity
- Review application configuration
- Ensure all required services are available

### Issue: High response times
**Symptoms**: Slow API responses, UI delays
**Causes**:
- Database query performance
- Insufficient resources
- High traffic load
- External service latency
**Solutions**:
- Optimize database queries
- Scale resources
- Implement caching
- Review external service dependencies

### Issue: Authentication problems
**Symptoms**: Users unable to login, API access denied
**Causes**:
- JWT configuration issues
- Time synchronization problems
- Token validation errors
**Solutions**:
- Check JWT secret configuration
- Verify system time synchronization
- Review security configuration

## Monitoring and Health Checks

### Health Check Endpoints
- Backend: `GET /actuator/health` - Application health
- Frontend: `GET /health` - Frontend health
- Database: Connection to PostgreSQL
- Redis: Connection to Redis
- RabbitMQ: Connection to message broker

### Key Metrics to Monitor
- Response times (P95, P99)
- Error rates
- Throughput (requests per second)
- Resource usage (CPU, memory, disk)
- Database connection pool metrics
- Cache hit/miss ratios

### Alert Thresholds
- Error rate > 5%
- Response time P95 > 2 seconds
- Response time P99 > 5 seconds
- CPU usage > 85%
- Memory usage > 85%
- Database connection pool > 90% used

### Monitoring Commands

**Kubernetes:**
```bash
# Check pod status
kubectl get pods -n myproject-nexa

# Check service status
kubectl get svc -n myproject-nexa

# Check resource usage
kubectl top pods -n myproject-nexa

# Check logs
kubectl logs -f deployment/myproject-nexa-backend -n myproject-nexa
```

**Database:**
```sql
-- Check active connections
SELECT count(*) FROM pg_stat_activity;

-- Check slow queries
SELECT query, mean_time FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;
```

### Emergency Contacts
- **Production Issues**: dev-team@myproject.nexa
- **Security Issues**: security@myproject.nexa
- **Infrastructure Issues**: ops-team@myproject.nexa