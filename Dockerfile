FROM gradle:jdk-25-and-25-alpine AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY gradlew ./
COPY src ./src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]