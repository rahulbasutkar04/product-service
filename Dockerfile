# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle* .

# Make gradlew executable and download dependencies (cached if build files unchanged)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy source and build the application
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Expose application port (dev profile uses 8081; override with -e SERVER_PORT=8080 if needed)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
