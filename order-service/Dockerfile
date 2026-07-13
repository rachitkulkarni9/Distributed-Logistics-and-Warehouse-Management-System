FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY common/src common/src
COPY order-service/pom.xml order-service/pom.xml
RUN mvn -B dependency:go-offline -pl order-service -am -q
COPY order-service/src order-service/src
RUN mvn -B package -pl order-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app
COPY --from=builder /build/order-service/target/order-service-*.jar app.jar
RUN chown logistics:logistics app.jar
USER logistics
EXPOSE 8082
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
