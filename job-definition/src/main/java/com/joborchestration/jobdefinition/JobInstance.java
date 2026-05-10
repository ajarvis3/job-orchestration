package com.joborchestration.jobdefinition;

import java.util.Map;

public record JobInstance(
        Long id,
        String jobType,
        JobState state,
        Map<String, Object> params) {
}

