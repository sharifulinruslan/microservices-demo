# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml ./pom.xml

# Copy service POM and source code
COPY payment-service/pom.xml ./payment-service/pom.xml
COPY payment-service/src ./payment-service/src

# Build the service
WORKDIR /app/payment-service
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar /app/payment-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "payment-service.jar"]