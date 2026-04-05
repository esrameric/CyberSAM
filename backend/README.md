# CyberSAM Backend - Spring Boot 3.x

Professional-grade Software Asset Management API with defense industry standards compliance.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 16 or AWS RDS
- Docker (optional)

### Build & Run

```bash
# Build with Maven
mvn clean package

# Run application
java -jar target/cybersam-backend-1.0.0.jar

# Or use Spring Boot plugin
mvn spring-boot:run

# Access at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Docker Build

```bash
# Build Docker image
docker build -t cybersam-backend:1.0.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/cybersam_db \
  -e SPRING_DATASOURCE_USERNAME=cybersam_user \
  -e SPRING_DATASOURCE_PASSWORD=CyberSAM@Secure123 \
  cybersam-backend:1.0.0
```

## Project Structure

```
src/main/java/com/cybersam/
├── CyberSAMApplication.java      # Spring Boot entry point
├── entity/
│   └── SoftwareAsset.java         # JPA Entity with lifecycle hooks
├── dto/
│   └── SoftwareAssetDTO.java      # Data transfer object
├── repository/
│   └── SoftwareAssetRepository.java # Specialized JPA queries
├── service/
│   └── SoftwareAssetService.java  # Business logic layer
├── controller/
│   └── SoftwareAssetController.java # REST endpoints with Swagger
└── config/
    └── OpenAPIConfig.java         # Springdoc-OpenAPI configuration

src/main/resources/
└── application.properties          # Database & Spring configuration
```

## Key Features

### SoftwareAsset Entity
- UUID auto-generation
- Automatic timestamp management
- Computed properties: `isExpired()`, `isCritical()`
- PostgreSQL table: `software_assets` (public schema)

### Advanced Queries
```java
// License Management
findExpiredLicenses()
findLicensesExpiringWithin(days)

// Vulnerability Management
findVulnerableAssets()
findCriticalAssets()

// Reporting
countVulnerableAssets()
countExpiredLicenses()
countCriticalAssets()
```

### REST API (Swagger Documented)

**Base URL**: `/api/v1/software-assets`

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | / | Create new asset |
| GET | / | List all assets |
| GET | /{id} | Retrieve specific asset |
| PUT | /{id} | Update asset |
| DELETE | /{id} | Remove asset |
| GET | /expired/licenses | Get expired licenses |
| GET | /expiring/within?days=X | Get licenses expiring soon |
| GET | /vulnerable | Get vulnerable assets |
| GET | /critical | Get critical assets (expired OR vulnerable) |
| GET | /vendor/{name} | Get assets by vendor |
| GET | /search?q=pattern | Search by name |
| GET | /stats/dashboard | Get dashboard metrics |

### Swagger Integration
- OpenAPI 3.0 specification
- Interactive documentation at `/swagger-ui.html`
- API schema at `/v3/api-docs`
- Customizable servers (dev/prod)

## Configuration

### Database (PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cybersam_db
spring.datasource.username=cybersam_user
spring.datasource.password=CyberSAM@Secure123
spring.jpa.hibernate.ddl-auto=update
```

### AWS RDS
```properties
spring.datasource.url=jdbc:postgresql://{RDS_ENDPOINT}:5432/cybersam_db
spring.datasource.username={RDS_USERNAME}
spring.datasource.password={RDS_PASSWORD}
```

### Server
```properties
server.port=8080
server.servlet.context-path=/
```

### Logging
```properties
logging.level.com.cybersam=DEBUG
logging.level.org.springframework.web=INFO
```

## Deployment

### Docker Compose
```bash
docker-compose up -d backend postgres
```

### AWS ECS/Fargate
```bash
# Push to ECR
docker tag cybersam-backend:1.0.0 {AWS_ACCOUNT}.dkr.ecr.{REGION}.amazonaws.com/cybersam-backend:1.0.0
docker push {AWS_ACCOUNT}.dkr.ecr.{REGION}.amazonaws.com/cybersam-backend:1.0.0

# Configure task definition with environment variables
```

### Kubernetes
```bash
# Build and push image
docker build -t cybersam-backend:1.0.0 .

# Deploy
kubectl apply -f deployment.yaml
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SoftwareAssetControllerTest

# Generate coverage report
mvn test jacoco:report
```

## Environment Variables

| Variable | Default | Purpose |
|----------|---------|---------|
| SPRING_DATASOURCE_URL | localhost:5432 | Database connection |
| SPRING_DATASOURCE_USERNAME | cybersam_user | DB username |
| SPRING_DATASOURCE_PASSWORD | - | DB password (required) |
| SPRING_JPA_HIBERNATE_DDL_AUTO | update | Schema generation |
| SERVER_PORT | 8080 | API port |

## Dependencies

- **Spring Boot 3.2.0**: Web framework
- **Spring Data JPA**: ORM & database abstraction
- **PostgreSQL**: Production database
- **Springdoc OpenAPI 2.1.0**: Swagger/OpenAPI integration
- **Lombok**: Reduce boilerplate
- **Jakarta Validation**: Input validation

## API Usage Examples

### Create Asset
```bash
curl -X POST http://localhost:8080/api/v1/software-assets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "OpenSSL",
    "version": "3.0.7",
    "vendor": "OpenSSL Software Foundation",
    "expiryDate": "2025-12-31",
    "isVulnerable": true,
    "cveNotes": "CVE-2023-0464: Potential infinite loop in X.509 certificate verification",
    "description": "Critical cryptography library"
  }'
```

### Get Dashboard Stats
```bash
curl http://localhost:8080/api/v1/software-assets/stats/dashboard
```

### Get Vulnerable Assets
```bash
curl http://localhost:8080/api/v1/software-assets/vulnerable
```

## Performance

- **Connection Pool**: HikariCP (10 max, 2 min)
- **Batch Operations**: Enabled (batch_size=20)
- **Query Optimization**: Indexed on id, vendor, expiryDate
- **Response Time**: < 200ms for typical queries

## Security

- No public data exposure (private fields)
- Input validation on all endpoints
- CORS configuration
- SQL injection prevention (prepared statements)
- Entity-level security annotations

## Monitoring

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Database Connection Failed
```bash
# Check PostgreSQL is running
psql -h localhost -U cybersam_user -d cybersam_db

# Verify credentials in application.properties
# For Docker: use 'postgres' as hostname instead of 'localhost'
```

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081

# Or kill existing process
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

### Swagger UI Not Loading
- Verify springdoc-openapi dependency is present
- Check /swagger-ui.html endpoint
- Ensure Spring context path is correct

## Contributing

1. Create feature branch from `main`
2. Follow Spring Boot conventions
3. Add unit tests for new endpoints
4. Run `mvn clean test` before committing
5. Submit PR with clear description

---

**Version**: 1.0.0  
**Java**: 17+  
**Spring Boot**: 3.2.0  
**Status**: Production Ready
