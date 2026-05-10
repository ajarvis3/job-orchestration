package com.joborchestration.jobdefinition;

import java.time.Duration;
import java.util.Map;

public record JobDefinition(
        String jobType,
        String description,
        Map<String, String> parameters,
        Duration timeout,
        int maxRetries) {
}

