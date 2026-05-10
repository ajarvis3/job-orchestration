package com.joborchestration.orchestrator.api;

import com.joborchestration.orchestrator.api.dto.CreateImageCompressionRequest;
import com.joborchestration.orchestrator.api.dto.CreateJobRequest;
import com.joborchestration.orchestrator.api.dto.ImageCompressionGrantResponse;
import com.joborchestration.orchestrator.api.dto.JobResponse;
import com.joborchestration.orchestrator.service.JobService;
import com.joborchestration.orchestrator.storage.ImageCompressionStorageService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;
    private final ImageCompressionStorageService imageCompressionStorageService;

    public JobController(JobService jobService, ImageCompressionStorageService imageCompressionStorageService) {
        this.jobService = jobService;
        this.imageCompressionStorageService = imageCompressionStorageService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request, jwt.getSubject()));
    }

    @PostMapping("/image-compression")
    public ResponseEntity<ImageCompressionGrantResponse> createImageCompressionGrant(
            @Valid @RequestBody CreateImageCompressionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createImageCompressionGrant(request, jwt.getSubject()));
    }

    @PutMapping(value = "/image-compression-put/{storageKey}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobResponse> uploadImageCompression(
            @PathVariable String storageKey,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        String fileUri = imageCompressionStorageService.store(storageKey, file);
        CreateJobRequest request = new CreateJobRequest(
                "image-compression",
                new LinkedHashMap<>(java.util.Map.of("uri", fileUri)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, jwt.getSubject()));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jobService.getJob(jobId, jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> listJobs(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(jobService.listJobs(jwt.getSubject(), offset, limit));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId, @AuthenticationPrincipal Jwt jwt) {
        jobService.deleteJob(jobId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
