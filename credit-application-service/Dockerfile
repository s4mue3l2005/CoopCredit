# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar POM padre primero (necesario para resolver dependencias)
COPY pom.xml .
# Copiar POMs de los módulos
COPY credit-application-service/pom.xml ./credit-application-service/
COPY risk-central-mock-service/pom.xml ./risk-central-mock-service/
# Copiar código fuente del módulo credit-application-service
COPY credit-application-service/src ./credit-application-service/src

# Compilar solo el módulo credit-application-service y sus dependencias
RUN mvn clean package -pl credit-application-service -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/credit-application-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
