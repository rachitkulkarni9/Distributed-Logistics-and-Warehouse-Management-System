# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent POM and common module first for layer caching
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY api-gateway/pom.xml api-gateway/pom.xml

# Download dependencies (cached unless POMs change)
RUN mvn -B dependency:go-offline -pl api-gateway -am -q

# Build
COPY api-gateway/src api-gateway/src
RUN mvn -B package -pl api-gateway -am -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S logistics && adduser -S logistics -G logistics

WORKDIR /app

COPY --from=builder /build/api-gateway/target/api-gateway-*.jar app.jar

RUN chown logistics:logistics app.jar

USER logistics

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
