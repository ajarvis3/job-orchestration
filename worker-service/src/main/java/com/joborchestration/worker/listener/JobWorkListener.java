package com.joborchestration.worker.listener;

import com.joborchestration.jobdefinition.JobInstance;
import com.joborchestration.jobdefinition.JobEvent;
import com.joborchestration.worker.service.JobExecutionService;
import java.util.logging.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
class JobWorkListener {

    private static final Logger log = Logger.getLogger(JobWorkListener.class.getName());

    private final JobExecutionService jobExecutionService;

    JobWorkListener(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    @KafkaListener(topics = "${worker.kafka.work-topic:job-work}")
    void onJob(@Payload JobInstance jobInstance) {
        JobEvent event = jobExecutionService.execute(jobInstance);
        log.info(() -> "Executed job " + jobInstance.id() + " and produced event " + event.eventType());
    }
}
