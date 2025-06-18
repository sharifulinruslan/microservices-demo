# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent POM first
COPY pom.xml ./pom.xml

# Copy service POM and source code
COPY user-service/pom.xml ./user-service/pom.xml
COPY user-service/src ./user-service/src

# Build the service
WORKDIR /app/user-service
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar /app/user-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user-service.jar"]