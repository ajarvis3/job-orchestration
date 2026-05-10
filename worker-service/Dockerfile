FROM gradle:9.4.1-jdk17 AS build
WORKDIR /workspace

# Copy the multi-project build metadata first so Docker can cache dependency resolution.
COPY settings.gradle build.gradle ./
COPY job-definition ./job-definition
COPY orchestrator-service ./orchestrator-service
COPY worker-service ./worker-service

RUN gradle :worker-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/worker-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
