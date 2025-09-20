
# ---------------------------------------------------------
# Stage 1: Build Spring Boot Application
# ---------------------------------------------------------

# Use Maven with Eclipse Temurin JDK for Java 21 apps
FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /build

# Copy Maven project files
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application and skip tests to speed up the build process
RUN mvn clean package -DskipTests

# ---------------------------------------------------------
# Stage 2: Run Spring Boot Application
# ---------------------------------------------------------

# Use Eclipse Temurin JDK for Java 21 apps
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR file
#COPY target/core-pos-0.0.1-SNAPSHOT.jar /app/core-pos.jar
COPY --from=builder /build/target/core-pos-0.0.1-SNAPSHOT.jar app.jar

# Expose port (change if your app uses a different port)
EXPOSE 5000

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]