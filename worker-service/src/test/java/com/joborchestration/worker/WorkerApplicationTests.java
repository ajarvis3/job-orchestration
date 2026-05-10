package com.joborchestration.worker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "worker.kafka.enabled=false"
})
class WorkerApplicationTests {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}
