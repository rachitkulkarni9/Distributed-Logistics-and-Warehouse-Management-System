FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY inventory-service/pom.xml inventory-service/pom.xml
RUN mvn -B dependency:go-offline -pl inventory-service -am -q
COPY inventory-service/src inventory-service/src
RUN mvn -B package -pl inventory-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app
COPY --from=builder /build/inventory-service/target/inventory-service-*.jar app.jar
RUN chown logistics:logistics app.jar
USER logistics
EXPOSE 8083
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
