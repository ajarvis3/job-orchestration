package com.joborchestration.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "worker.kafka")
public record WorkerProperties(
        String workTopic,
        String eventTopic) {
}

