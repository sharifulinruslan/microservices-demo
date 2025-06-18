# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml ./pom.xml

# Copy service POM and source code
COPY product-service/pom.xml ./product-service/pom.xml
COPY product-service/src ./product-service/src

# Build the service
WORKDIR /app/product-service
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar /app/product-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "product-service.jar"]