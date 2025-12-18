package com.loiane.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.loiane.config.TestContainersConfiguration;

/**
 * Abstract base class for integration tests using TestContainers.
 * This class sets up a complete Spring Boot application context with
 * a real MySQL database running in a Docker container.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    protected TestRestTemplate restTemplate;

    @BeforeEach
    void setUpRestTemplate() {
        restTemplate = new TestRestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
    }

    /**
     * Helper method to build the full URL for API endpoints
     */
    protected String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }

    /**
     * Helper method to build URLs for course-related endpoints
     */
    protected String buildCourseUrl(String path) {
        return buildUrl("/api/courses" + path);
    }
}
