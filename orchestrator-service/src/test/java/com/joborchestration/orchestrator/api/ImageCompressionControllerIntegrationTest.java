package com.joborchestration.orchestrator.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImageCompressionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createImageCompressionGrantReturnsGrant() throws Exception {
        mockMvc.perform(post("/api/v1/jobs/image-compression")
                        .with(jwt().jwt(token -> token.subject("user-a")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"filename":"photo.png","type":"image/png"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.storageKey").exists())
                .andExpect(jsonPath("$.uploadMethod").value("PUT"))
                .andExpect(jsonPath("$.job.jobType").value("image-compression"))
                .andExpect(jsonPath("$.job.requestedBy").value("user-a"));
    }

    @Test
    void uploadImageCompressionCreatesJobWithUri() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "test".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/jobs/image-compression-put/{storageKey}", "storage-123")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(jwt().jwt(token -> token.subject("user-a"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobType").value("image-compression"))
                .andExpect(jsonPath("$.requestedBy").value("user-a"))
                .andExpect(jsonPath("$.params.uri").exists());
    }
}
