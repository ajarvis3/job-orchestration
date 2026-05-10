package com.joborchestration.orchestrator.service;

import com.joborchestration.orchestrator.api.dto.CreateJobRequest;
import com.joborchestration.orchestrator.api.dto.CreateImageCompressionRequest;
import com.joborchestration.orchestrator.api.dto.ImageCompressionGrantResponse;
import com.joborchestration.orchestrator.api.dto.JobResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {

    JobResponse createJob(CreateJobRequest request, String requestedBy);

    ImageCompressionGrantResponse createImageCompressionGrant(CreateImageCompressionRequest request, String requestedBy);

    JobResponse getJob(Long jobId, String requestedBy);

    List<JobResponse> listJobs(String requestedBy, int offset, int limit);

    void deleteJob(Long jobId, String requestedBy);

    void evaluateTimeouts();

    void evaluateRetries();
}
