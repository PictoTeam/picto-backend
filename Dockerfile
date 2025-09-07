# Build stage
FROM eclipse-temurin:24-jdk-alpine AS build

# Install necessary packages
RUN apk add --no-cache bash

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY gradle.properties .

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Build the application with layering enabled
RUN ./gradlew build -x test --no-daemon

# Extract layers from the JAR
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# Runtime stage
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 app && \
    adduser -u 1000 -G app -s /bin/sh -D app

# Copy layers in order of frequency of change (least to most frequent)
# Dependencies change least frequently
COPY --from=build /app/dependencies/ ./
# Spring Boot loader
COPY --from=build /app/spring-boot-loader/ ./
# Snapshot dependencies (if any)
COPY --from=build /app/snapshot-dependencies/ ./
# Application classes change most frequently
COPY --from=build /app/application/ ./

# Change ownership to app user
RUN chown -R app:app /app

# Switch to non-root user
USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
