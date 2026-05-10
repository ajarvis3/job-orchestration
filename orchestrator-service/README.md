# Orchestrator Service

Initial Spring Boot scaffold for the job orchestrator service.

## What is included

- Spring Boot application entrypoint
- JPA-backed job state entity and repository
- REST endpoints for create, fetch, list, and delete job records
- Scheduler hooks for timeout and retry processing
- Basic actuator and environment-driven configuration

## Local development

1. Set the datasource and Kafka environment variables as needed.
2. Run the app with Gradle:

```bash
gradle bootRun
```

## Actuator

Health endpoint:

```text
/actuator/health
```
