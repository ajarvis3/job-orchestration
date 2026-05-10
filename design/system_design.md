# System Design Document

## Template Metadata

| Field | Details |
|-------|---------|
| Category | Technical |
| Owner | Andrew Jarvis |
| Version | 1.0.0 |
| Effective Date | 2026-05-10 |
| Review Cycle | Event-Based |
| Status | Draft |

## Overview

This will be a simple job orchestration application. Users will be able to send
requests for jobs to be completed. The system will be able to scale to accomodate
higher workloads and will be widely available.

## Goals & Non-Goals

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| User can submit a job to be completed | Goal | Andrew Jarvis | Open |
| User can check job status | Goal | Andrew Jarvis | Open |
| Workers will scale under load | Goal | Andrew Jarvis | Open |
| The orchestrator will cancel jobs if they timeout | Goal | Andrew Jarvis | Open |
| The orchestrator will retry jobs with exponential backoffs | Goal | Andrew Jarvis | Open |
| User can cancel a job | Non-Goal | Andrew Jarvis | Open |

## Architecture

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| API Gateway | This is the entrance to the application. It will authorize the user before forwarding the request to the job orchestrator. | Andrew Jarvis | Open |
| EKS | The job orchestrator and workers will be provided as kubernetes services and will be managed by AWS's EKS. | Andrew Jarvis | Open |
| RDS | The service will use a relational database to store job status. | Andrew Jarvis | Open |
| Kafka | Kafka will be used for communication between the orchestrator and workers | Andrew Jarvis | Open |
| Cognito | Cognito will be used for user authentication/authorization. It will be checked at the API Gateway | Andrew Jarvis | Open |
| Orchestrator | Handles the incoming job requests and the state machine that updates those requests. Calls job workers as needed. | Andrew Jarvis | Open |
| Workers | Executes any tasks needed by a job given its state and parameters. Will use a pool of threads for execution. They will scale based on kafka queue depth using keda. Will periodically check on the status of the job during execution for timeouts or cancellations. | Andrew Jarvis | Open |
| Kafka | Communication between orchestrator and workers for events. Orchestrator sends jobs to the workers via a work topic which contains job id, type, and params. Workers will send events to the orchestrator via a status topic. | Andrew Jarvis | Open |
| gRPC | Worker will check on status during execution via RPC. | Andrew Jarvis | Open |
| Retries | If a job failure is reported or there is a timeout, a job will be retried at most three times. They will be retried at an interval of retry_count * 5 minutes. A scheduled job in the orchestrator will check every minute for job in TIMEOUT or FAILED status with fewer than three retries and an updated time greater than now - (retries * 5 minutes). | Andrew Jarvis | Open |
| Timeouts | Each job definition will contain information on how long a job can run before it is considered to be timed out. The orchestrator will check running jobs every 15 seconds for any timeouts based on when they were last updated to a running status. If it is timed out, the state in the database will be updated to TIMEDOUT and this will become available to the worker when it checks for status | Andrew Jarvis | Open |
| Orchestrator Database | The orchestrator will communicate with the database via Jpa and connections configured through spring properties defined by environment variables or by application-properties based on the environment. | Andrew Jarvis | Open |
| Job States | Jobs will run one state at a time based on information in the database and coming from the kafka status topic. Database jobs will run based on schedules and states that should allow the job to be queued (i.e. not queued and not running). The orchestrator will update states accordingly when it receives status updates e.g. if it is in the running state, the database will update the state to running. | Andrew Jarvis | Open |

## Data Model

Key entities, relationships, and storage choices. Use tables or diagrams.

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| Relational Database | The data is structured, so a relational database suits the needs of the project. | Andrew Jarvis | Open |
| JobStateEntity | Tracks job status in the database. Owned by the Orchestrator service. Will contain information on the job and its status. | Andrew Jarvis | Open |
| JobRequestDTO | Used to send data to create a new job. It will include a job type and parameters needed to run that job. {id: long, params: object} | Andrew Jarvis | Open |
| JobResponseDTO | Used to send back Job status. Returns with id and job state. | Andrew Jarvis | Open |
| JobDefinition | The definition of a job. Contains information about the job type, description, parameter information, and execution details | Andrew Jarvis | Open |
| JobInstance | An instance of a given job for the application. Contains information necessary to run a job, but not the execution details | Andrew Jarvis | Open |
| JobState | An enum containing allowed job states such as CREATED, SCHEDULED, RUNNING, SUCCESS, FAILURE, TIMEOUT | Andrew Jarvis | Open |
| JobParameters | A HashMap to contain a job's parameters | Andrew Jarvis | Open |
| JobStateMachine | Runs individuals jobs and handles the transitions between states. Calls the worker as needed to complete work. | Andrew Jarvis | Open |


## API Design

Core endpoints or interfaces with request/response examples.

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| POST /api/v1/jobs | Creates a new Job based on the information in the request body. The initial state of the job will be returned including its id. | Andrew Jarvis | Open |
| GET /api/v1/jobs/{jobId} | Gets the current state of a job given its id | Andrew Jarvis | Open |
| GET /api/v1/jobs?offset=x&limit=y | Gets all jobs available to the user. Takes optional offset and limit | Andrew Jarvis | Open |
| DELETE /api/v1/jobs/{jobId} | Deletes a job by id. Returns whether successful | Andrew Jarvis | Open |

## Security

Authentication, authorization, data protection, and threat model.

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| Cognito Auth | Cognito will be used for user auth flows. | Andrew Jarvis | Open |

## Deployment & Monitoring

Infrastructure, CI/CD, observability, and alerting. Use Markdown formatting with code blocks and tables.

| Item | Details | Owner | Status |
|------|---------|-------|--------|
| GitHub | GitHub will be used as the repository for code and as the initial CI/CD for the application. | Andrew Jarvis | Open |
| AWS | AWS infrastructure will be used for hosting the actual application. | Andrew Jarvis | Open |
| Cloudwatch + Managed Grafana | This will be used for observability into the application when deployed in AWS | Andrew Jarvis | Open |
| Prometheus + Grafana | This will be used for development deploys | Andrew Jarvis | Open |
| Cloudwatch Alarms | This will be used for any alerting as needed | Andrew Jarvis | Open |

## Review and Signoff

Document review conclusions, approvals, unresolved items, and next review date.

| Role | Name | Date | Notes |
|------|------|------|-------|
| Preparer | [Name] | [Date] | [Notes] |
| Reviewer | [Name] | [Date] | [Notes] |
| Approver | [Name] | [Date] | [Notes] |
