package com.joborchestration.orchestrator.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class LocalImageCompressionStorageService implements ImageCompressionStorageService {

    private final Path storageRoot;

    LocalImageCompressionStorageService(
            @Value("${orchestrator.image-compression.storage-root:./build/image-compression}") String storageRoot) {
        this.storageRoot = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    @Override
    public String store(String storageKey, MultipartFile file) {
        try {
            Files.createDirectories(storageRoot);
            Path target = storageRoot.resolve(storageKey).normalize();
            if (!target.startsWith(storageRoot)) {
                throw new IllegalArgumentException("Invalid storage key");
            }
            Files.createDirectories(target.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return target.toUri().toString();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store image compression upload", exception);
        }
    }
}
