FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/*.jar /app/order-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "order-service.jar"]