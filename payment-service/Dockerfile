FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/*.jar /app/payment-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "payment-service.jar"]