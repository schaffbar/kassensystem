# Use Eclipse Temurin JDK for Java 21 apps
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/core-pos-0.0.1-SNAPSHOT.jar /app/core-pos.jar

# Expose port (change if your app uses a different port)
EXPOSE 5000

# Run the JAR
CMD ["java", "-jar", "core-pos.jar"]
