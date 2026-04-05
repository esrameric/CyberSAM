# Multi-Stage Build for CyberSAM Backend
# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests=true

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-alpine

# Set security labels
LABEL maintainer="CyberSAM Security Team"
LABEL description="Secure Software Asset Management - Defense Industry Standard"
LABEL version="1.0.0"

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 cybersam && \
    adduser -D -u 1001 -G cybersam cybersam

# Copy JAR from builder
COPY --from=builder /build/target/cybersam-backend-1.0.0.jar app.jar
RUN chown cybersam:cybersam /app/app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Switch to non-root user
USER cybersam:cybersam

# Expose port
EXPOSE 8080

# Container entry point
ENTRYPOINT ["java", "-jar", "app.jar"]