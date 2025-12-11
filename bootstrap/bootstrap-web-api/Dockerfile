# ============================================================================
# AuthHub Web API - Production Dockerfile
# ============================================================================
# Multi-stage build for optimized image size
# Base: Eclipse Temurin JRE 21 (Alpine)
# ============================================================================

# Stage 1: Builder (JAR extraction for layered approach)
FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /app
COPY bootstrap/bootstrap-web-api/build/libs/AuthHub-web-api.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Security: Run as non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy extracted layers (for better caching)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# JVM Configuration
ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=200 \
               -XX:+UseStringDeduplication \
               -XX:InitialRAMPercentage=50.0 \
               -XX:MaxRAMPercentage=75.0 \
               -Djava.security.egd=file:/dev/./urandom"

# Application Configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
