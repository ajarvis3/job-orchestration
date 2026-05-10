package com.joborchestration.orchestrator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.joborchestration.orchestrator.api.dto.CreateJobRequest;
import com.joborchestration.orchestrator.api.dto.JobResponse;
import com.joborchestration.orchestrator.domain.JobState;
import com.joborchestration.orchestrator.domain.JobStateEntity;
import com.joborchestration.orchestrator.exception.JobNotFoundException;
import com.joborchestration.orchestrator.repository.JobStateRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JobServiceOwnershipIntegrationTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobStateRepository jobStateRepository;

    @BeforeEach
    void setUp() {
        jobStateRepository.deleteAll();
    }

    @Test
    void listJobsOnlyReturnsJobsForMatchingSubject() {
        jobService.createJob(new CreateJobRequest("noop", Map.of("delaySeconds", 1)), "user-a");
        jobService.createJob(new CreateJobRequest("noop", Map.of("delaySeconds", 2)), "user-a");
        jobService.createJob(new CreateJobRequest("noop", Map.of("delaySeconds", 3)), "user-b");

        List<JobResponse> userAJobs = jobService.listJobs("user-a", 0, 10);
        List<JobResponse> userBJobs = jobService.listJobs("user-b", 0, 10);

        assertEquals(2, userAJobs.size());
        assertEquals(1, userBJobs.size());
        assertEquals(Map.of(), userAJobs.get(0).outputParams());
    }

    @Test
    void getJobRejectsJobsOwnedByAnotherSubject() {
        JobStateEntity saved = jobStateRepository.save(job("user-a"));

        assertThrows(JobNotFoundException.class, () -> jobService.getJob(saved.getId(), "user-b"));
    }

    @Test
    void deleteJobRejectsJobsOwnedByAnotherSubject() {
        JobStateEntity saved = jobStateRepository.save(job("user-a"));

        assertThrows(JobNotFoundException.class, () -> jobService.deleteJob(saved.getId(), "user-b"));
        assertTrue(jobStateRepository.existsById(saved.getId()));
    }

    private JobStateEntity job(String requestedBy) {
        JobStateEntity entity = new JobStateEntity();
        entity.setJobType("noop");
        entity.setParams(Map.of("delaySeconds", 1));
        entity.setRequestedBy(requestedBy);
        entity.setState(JobState.CREATED);
        entity.setRetries(0);
        return entity;
    }
}
