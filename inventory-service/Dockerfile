# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml ./pom.xml

# Copy service POM and source code
COPY inventory-service/pom.xml ./inventory-service/pom.xml
COPY inventory-service/src ./inventory-service/src

# Build the service
WORKDIR /app/inventory-service
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/inventory-service/target/*.jar /app/inventory-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]