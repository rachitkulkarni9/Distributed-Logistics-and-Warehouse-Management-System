FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn -B dependency:go-offline -pl notification-service -am -q
COPY notification-service/src notification-service/src
RUN mvn -B package -pl notification-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app
COPY --from=builder /build/notification-service/target/notification-service-*.jar app.jar
RUN chown logistics:logistics app.jar
USER logistics
EXPOSE 8086
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
