# syntax=docker/dockerfile:1

################################################################################

FROM eclipse-temurin:25-jdk-jammy as deps

WORKDIR /build

COPY --chmod=0755 gradlew gradlew
COPY gradle/ gradle/

RUN --mount=type=bind,source=build.gradle,target=build.gradle \
    --mount=type=bind,source=settings.gradle,target=settings.gradle \
    --mount=type=cache,target=/root/.gradle ./gradlew dependency:go-offline -DskipTests

################################################################################

FROM deps as package

WORKDIR /build

COPY ./src src/
RUN --mount=type=bind,source=build.gradle,target=build.gradle \
    --mount=type=bind,source=settings.gradle,target=settings.gradle \
    --mount=type=cache,target=/root/.gradle \
    ./gradlew package -DskipTests && \
    mv build/$(./gradlew help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./gradlew help:evaluate -Dexpression=project.version -q -DforceStdout).jar build/app.jar

################################################################################

FROM package as extract

WORKDIR /build

RUN java -Djarmode=layertools -jar build/app.jar extract --destination build/extracted

################################################################################

FROM eclipse-temurin:21-jre-jammy AS final

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

COPY --from=extract build/build/extracted/dependencies/ ./
COPY --from=extract build/build/extracted/spring-boot-loader/ ./
COPY --from=extract build/build/extracted/snapshot-dependencies/ ./
COPY --from=extract build/build/extracted/application/ ./

EXPOSE 9090

ENTRYPOINT [ "java", "-Dspring.profiles.active=postgres", "org.springframework.boot.loader.launch.JarLauncher" ]