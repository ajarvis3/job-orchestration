package com.joborchestration.orchestrator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "job_state")
public class JobStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobState state = JobState.CREATED;

    @Column(nullable = false)
    private int retries = 0;

    @Column
    private String requestedBy;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "params_json", nullable = false, length = 10_000)
    private Map<String, Object> params = new LinkedHashMap<>();

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "output_params_json", nullable = false, length = 10_000)
    private Map<String, Object> outputParams = new LinkedHashMap<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private Instant lastStateChangeAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        lastStateChangeAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
        this.lastStateChangeAt = Instant.now();
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(Map<String, Object> outputParams) {
        this.outputParams = outputParams;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastStateChangeAt() {
        return lastStateChangeAt;
    }
}
