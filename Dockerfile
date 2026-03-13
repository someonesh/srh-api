# Use the official Maven image to build the app
FROM maven:3.9.0-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use a lightweight Java image to run the app
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/backend_java-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]