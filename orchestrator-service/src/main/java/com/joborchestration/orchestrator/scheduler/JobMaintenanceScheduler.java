package com.joborchestration.orchestrator.scheduler;

import com.joborchestration.orchestrator.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobMaintenanceScheduler.class);

    private final JobService jobService;

    public JobMaintenanceScheduler(JobService jobService) {
        this.jobService = jobService;
    }

    @Scheduled(fixedDelayString = "${orchestrator.jobs.timeout-check-interval-ms:15000}")
    public void checkTimeouts() {
        log.debug("Running timeout check for active jobs");
        jobService.evaluateTimeouts();
    }

    @Scheduled(fixedDelayString = "${orchestrator.jobs.retry-check-interval-ms:60000}")
    public void checkRetries() {
        log.debug("Running retry check for eligible jobs");
        jobService.evaluateRetries();
    }
}
