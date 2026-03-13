# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o pom.xml primeiro (aproveita cache)
COPY pom.xml .

# Baixa dependências (opcional, mas ajuda no cache)
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila o projeto
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/backend_java-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]