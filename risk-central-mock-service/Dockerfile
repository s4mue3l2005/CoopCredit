# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar POM padre primero (necesario para resolver dependencias)
COPY pom.xml .
# Copiar POMs de los módulos
COPY risk-central-mock-service/pom.xml ./risk-central-mock-service/
COPY credit-application-service/pom.xml ./credit-application-service/
# Copiar código fuente del módulo risk-central-mock-service
COPY risk-central-mock-service/src ./risk-central-mock-service/src

# Compilar solo el módulo risk-central-mock-service y sus dependencias
RUN mvn clean package -pl risk-central-mock-service -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/risk-central-mock-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
