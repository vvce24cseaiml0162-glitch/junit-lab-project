# Multi-stage production-quality Dockerfile for JUnit 5 Test Suite execution
FROM maven:3.9.6-eclipse-temurin-17-alpine AS test-runner

# Set container working directory
WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and test files
COPY src ./src

# Create reports output directory
RUN mkdir -p /app/reports

# Declare volume mount point for persisting test reports and coverage to host
VOLUME ["/app/reports"]

# Execute JUnit test suite, generate JaCoCo coverage and copy artifacts
CMD ["sh", "-c", "mvn clean test jacoco:report && cp -rf target/surefire-reports target/site/jacoco /app/reports/"]
