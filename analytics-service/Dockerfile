FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY ../pom.xml ../pom.xml
COPY ../common ../common
RUN mvn dependency:go-offline -pl . -am -q
COPY src ./src
RUN mvn package -pl . -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
RUN chown logistics:logistics app.jar
USER logistics
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
