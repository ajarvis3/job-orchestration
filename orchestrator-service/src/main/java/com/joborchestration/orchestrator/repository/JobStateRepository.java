package com.joborchestration.orchestrator.repository;

import com.joborchestration.orchestrator.domain.JobStateEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobStateRepository extends JpaRepository<JobStateEntity, Long> {

    Optional<JobStateEntity> findByIdAndRequestedBy(Long id, String requestedBy);

    Page<JobStateEntity> findAllByRequestedByOrderByCreatedAtDesc(String requestedBy, Pageable pageable);

    boolean existsByIdAndRequestedBy(Long id, String requestedBy);

    long deleteByIdAndRequestedBy(Long id, String requestedBy);
}
