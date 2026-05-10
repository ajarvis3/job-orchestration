package com.joborchestration.orchestrator.domain;

public enum JobState {
    CREATED,
    QUEUED,
    RUNNING,
    SUCCESS,
    FAILURE,
    TIMEOUT
}
