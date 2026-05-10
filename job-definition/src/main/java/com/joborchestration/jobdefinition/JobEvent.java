package com.joborchestration.jobdefinition;

import java.util.Map;

public record JobEvent(
        JobEventType eventType,
        Long jobId,
        Map<String, Object> outputParams) {
}

