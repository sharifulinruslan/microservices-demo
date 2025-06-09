FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/*.jar /app/product-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "product-service.jar"]