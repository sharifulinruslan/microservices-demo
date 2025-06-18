# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml ./pom.xml

# Copy service POM and source code
COPY order-service/pom.xml ./order-service/pom.xml
COPY order-service/src ./order-service/src

# Build the service
WORKDIR /app/order-service
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar /app/order-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "order-service.jar"]