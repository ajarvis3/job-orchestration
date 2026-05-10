package com.joborchestration.orchestrator.service;

import com.joborchestration.orchestrator.api.dto.CreateImageCompressionRequest;
import com.joborchestration.orchestrator.api.dto.CreateJobRequest;
import com.joborchestration.orchestrator.api.dto.ImageCompressionGrantResponse;
import com.joborchestration.orchestrator.api.dto.JobResponse;
import com.joborchestration.jobdefinition.JobState;
import com.joborchestration.orchestrator.domain.JobStateEntity;
import com.joborchestration.orchestrator.exception.JobNotFoundException;
import com.joborchestration.orchestrator.repository.JobStateRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
class JobServiceImpl implements JobService {

    private final JobStateRepository jobStateRepository;

    JobServiceImpl(JobStateRepository jobStateRepository) {
        this.jobStateRepository = jobStateRepository;
    }

    @Override
    @Transactional
    public JobResponse createJob(CreateJobRequest request, String requestedBy) {
        JobStateEntity entity = new JobStateEntity();
        entity.setJobType(request.jobType());
        entity.setParams(request.params());
        entity.setRequestedBy(requestedBy);
        entity.setState(JobState.CREATED);
        entity.setRetries(0);
        return toResponse(jobStateRepository.save(entity));
    }

    @Override
    @Transactional
    public ImageCompressionGrantResponse createImageCompressionGrant(
            CreateImageCompressionRequest request, String requestedBy) {
        String uploadMethod = "PUT";
        String storageKey = "image-compression-" + UUID.randomUUID() + "-" + sanitizeFileName(request.filename());
        String uploadUrl = UriComponentsBuilder
                .fromPath("/api/v1/jobs/image-compression-put/{storageKey}")
                .buildAndExpand(storageKey)
                .toUriString();

        JobStateEntity entity = new JobStateEntity();
        entity.setJobType("image-compression");
        entity.setRequestedBy(requestedBy);
        entity.setParams(Map.of(
                "filename", request.filename(),
                "type", request.type()));
        entity.setOutputParams(new LinkedHashMap<>(Map.of(
                "storageKey", storageKey,
                "uploadUrl", uploadUrl,
                "uploadMethod", uploadMethod,
                "filename", request.filename(),
                "type", request.type())));
        entity.setState(JobState.CREATED);
        entity.setRetries(0);

        JobResponse job = toResponse(jobStateRepository.save(entity));
        return new ImageCompressionGrantResponse(job, storageKey, uploadUrl, uploadMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId, String requestedBy) {
        return toResponse(findJob(jobId, requestedBy));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> listJobs(String requestedBy, int offset, int limit) {
        int safeOffset = Math.max(offset, 0);
        int safeLimit = Math.max(limit, 1);
        return jobStateRepository.findAllByRequestedByOrderByCreatedAtDesc(
                        requestedBy,
                        PageRequest.of(safeOffset, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toResponse)
                .getContent();
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, String requestedBy) {
        if (!jobStateRepository.existsByIdAndRequestedBy(jobId, requestedBy)) {
            throw new JobNotFoundException(jobId);
        }
        jobStateRepository.deleteByIdAndRequestedBy(jobId, requestedBy);
    }

    @Override
    public void evaluateTimeouts() {
        // Initial scaffold hook for the timeout scheduler.
    }

    @Override
    public void evaluateRetries() {
        // Initial scaffold hook for the retry scheduler.
    }

    private JobStateEntity findJob(Long jobId, String requestedBy) {
        return jobStateRepository.findByIdAndRequestedBy(jobId, requestedBy)
                .orElseThrow(() -> new JobNotFoundException(jobId));
    }

    private JobResponse toResponse(JobStateEntity entity) {
        return new JobResponse(
                entity.getId(),
                entity.getJobType(),
                entity.getState(),
                entity.getRetries(),
                entity.getRequestedBy(),
                entity.getParams(),
                entity.getOutputParams(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastStateChangeAt());
    }

    private String sanitizeFileName(String filename) {
        String sanitized = filename.replaceAll("[^A-Za-z0-9._-]", "-");
        return sanitized.isBlank() ? "file" : sanitized;
    }
}
