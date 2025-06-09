FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/*.jar /app/user-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user-service.jar"]