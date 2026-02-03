# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/
COPY user-service/pom.xml user-service/
COPY task-service/pom.xml task-service/
COPY notification-service/pom.xml notification-service/

RUN mvn dependency:go-offline -B

COPY common/src ./common/src
COPY user-service/src ./user-service/src
COPY task-service/src ./task-service/src
COPY notification-service/src ./notification-service/src

RUN mvn clean package -pl :notification-service -am -DskipTests -B


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
