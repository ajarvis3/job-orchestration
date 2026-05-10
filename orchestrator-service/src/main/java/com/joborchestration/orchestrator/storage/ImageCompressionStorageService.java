package com.joborchestration.orchestrator.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageCompressionStorageService {

    String store(String storageKey, MultipartFile file);
}
