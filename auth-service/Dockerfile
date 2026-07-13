# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY auth-service/pom.xml auth-service/pom.xml

RUN mvn -B dependency:go-offline -pl auth-service -am -q

COPY auth-service/src auth-service/src
RUN mvn -B package -pl auth-service -am -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S logistics && adduser -S logistics -G logistics

WORKDIR /app

COPY --from=builder /build/auth-service/target/auth-service-*.jar app.jar

RUN chown logistics:logistics app.jar

USER logistics

EXPOSE 8081

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
