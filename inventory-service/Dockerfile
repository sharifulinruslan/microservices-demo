FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/*.jar /app/inventory-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]