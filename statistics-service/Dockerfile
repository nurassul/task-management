# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/
COPY user-service/pom.xml user-service/
COPY task-service/pom.xml task-service/
COPY notification-service/pom.xml notification-service/
COPY api-gateway/pom.xml api-gateway/
COPY statistics-service/pom.xml statistics-service/

RUN mvn dependency:go-offline -B

COPY common/src ./common/src
COPY user-service/src ./user-service/src
COPY task-service/src ./task-service/src
COPY notification-service/src ./notification-service/src
COPY api-gateway/src ./api-gateway/src
COPY statistics-service/src ./statistics-service/src

RUN mvn clean package -pl :statistics-service -am -DskipTests -B


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/statistics-service/target/statistics-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
