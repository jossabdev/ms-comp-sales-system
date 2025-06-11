# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy

#WORKDIR /app

#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#RUN ./mvnw dependency:resolve

#COPY src ./src

#CMD ["./mvnw", "spring-boot:run"]

WORKDIR /app

RUN apt update && apt install tzdata -y
ENV TZ="America/Guayaquil"

# Copy the JAR file to the container
COPY target/ms-comp-sales-system-1.0.0-SNAPSHOT.jar app.jar
COPY application.properties.dist application.properties
# Expose the port that your Spring Boot application listens on (default is 8080)
EXPOSE 1600
# Define the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.config.location=/app/application.properties"]