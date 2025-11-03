# STAGE 1: BUILDER
# Use the official Maven image with Java 21 to compile the application
FROM maven:3.9.5-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /workspace

# Copy the pom.xml and the source code (src directory)
COPY pom.xml .
COPY src /workspace/src

# Run Maven to compile and package the application
# This is where the JAR file: facebookapi-0.0.1-SNAPSHOT.jar is created
RUN mvn package -DskipTests

# ---

# STAGE 2: RUNNER (Final Image)
# Use a lightweight JRE base image (smaller for production)
FROM eclipse-temurin:21-jre-alpine

# Set the application directory
WORKDIR /app

# **CRITICAL STEP: Use the exact JAR filename you found previously**
# COPY --from=builder /workspace/target/YOUR-JAR-FILENAME.jar /app/app.jar
COPY --from=builder /workspace/target/facebookapi-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Command to run the executable JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]