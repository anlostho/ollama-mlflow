# Use a base image with Java 17 and Maven
FROM maven:3.9.6-amazoncorretto-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
# Download the dependencies
RUN mvn dependency:go-offline -B

COPY src ./src

# Build the application with Maven, the test are not going to execute
RUN mvn clean package -DskipTests

# Use a minimal JRE image for runtime
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar ./spring-ai-service.jar

# Specify the command to run your Spring Boot application
CMD ["java", "-jar", "spring-ai-service.jar"]

