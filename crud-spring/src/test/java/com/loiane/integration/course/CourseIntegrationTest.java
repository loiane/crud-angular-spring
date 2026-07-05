package com.loiane.integration.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.loiane.course.Course;
import com.loiane.course.CourseRepository;
import com.loiane.course.CourseTestRepository;
import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CoursePageDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.dto.LessonDTO;
import com.loiane.course.enums.Status;
import com.loiane.integration.AbstractIntegrationTest;
import com.loiane.shared.controller.ApplicationControllerAdvice.FieldValidationError;

/**
 * Integration tests for Course API endpoints using TestContainers.
 * These tests run against a real MySQL database in a Docker container,
 * providing complete end-to-end testing.
 */
@DisplayName("Course API Integration Tests")
class CourseIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_YOUTUBE_URL = "dQw4w9WgXcQ";

    /**
     * Subset of the RFC 7807 Problem Details response returned by the API,
     * including the custom "errors" property added for validation failures.
     */
    record ProblemResponse(String detail, List<FieldValidationError> errors) {
    }

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseTestRepository courseTestRepository;

    @AfterEach
    void cleanUp() {
        // Clean up database after each test to avoid data contamination
        // Use try-catch in case schema is being recreated
        try {
            courseRepository.deleteAll();
        } catch (Exception e) {
            // Ignore cleanup errors as they're typically due to schema recreation
            System.out.println("Cleanup warning (can be ignored): " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should retrieve all courses with pagination")
    void testFindAllCourses() {
        // Given - Create test data directly
        createAndSaveCourseDirect("Java Fundamentals", "back-end");
        createAndSaveCourseDirect("Angular Fundamentals", "front-end");
        createAndSaveCourseDirect("Spring Boot", "back-end");

        // When - Call the API
        ResponseEntity<CoursePageDTO> response = restTemplate.getForEntity(
                "/api/courses?page=0&size=10",
                CoursePageDTO.class);

        // Then - Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        CoursePageDTO coursePageDTO = response.getBody();
        assertNotNull(coursePageDTO);
        assertThat(coursePageDTO.courses()).isNotEmpty();
        assertThat(coursePageDTO.totalElements()).isEqualTo(coursePageDTO.courses().size());
        assertThat(coursePageDTO.totalPages()).isEqualTo(1);

        // Verify the courses contain our test data
        List<String> courseNames = coursePageDTO.courses().stream()
                .map(CourseDTO::name)
                .toList();
        assertThat(courseNames).containsExactlyInAnyOrder(
                "Java Fundamentals", "Angular Fundamentals", "Spring Boot");
    }

    @Test
    @DisplayName("Should retrieve a course by ID")
    void testFindCourseById() {
        // Given - Create a test course using repository (will be committed)
        Course savedCourse = createAndSaveCourseDirect("Spring Boot Advanced", "Back-end");

        // When - Call the API
        String url = buildCourseUrl("/" + savedCourse.getId());
        ResponseEntity<CourseDTO> response = restTemplate.getForEntity(url, CourseDTO.class);

        // Then - Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CourseDTO courseDTO = response.getBody();
        assertNotNull(courseDTO);

        assertEquals("Spring Boot Advanced", courseDTO.name());
        assertEquals("Back-end", courseDTO.category());
        assertThat(courseDTO.lessons()).hasSize(1);
    }

    @Test
    @DisplayName("Should return 404 when course not found")
    void testFindCourseByIdNotFound() {
        // When - Call the API with non-existent ID
        String url = buildCourseUrl("/999");
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then - Verify 404 response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody()).contains("Could not find record 999");
    }

    @Test
    @DisplayName("Should create a new course successfully")
    void testCreateCourse() {
        // Given - Create a valid course request
        CourseRequestDTO courseRequest = createValidCourseRequest("Spring Security", "Back-end");

        // When - Call the API
        String url = buildCourseUrl("");
        ResponseEntity<CourseDTO> response = restTemplate.postForEntity(url, courseRequest, CourseDTO.class);

        // Then - Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CourseDTO createdCourse = response.getBody();
        assertNotNull(createdCourse);

        assertEquals("Spring Security", createdCourse.name());
        assertEquals("Back-end", createdCourse.category());
        assertNotNull(createdCourse.id());

        // Verify it was actually saved in the database
        Course savedCourse = courseRepository.findById(createdCourse.id()).orElse(null);
        assertNotNull(savedCourse);
        assertEquals(Status.ACTIVE, savedCourse.getStatus());
    }

    @Test
    @DisplayName("Should reject invalid course creation")
    void testCreateCourseWithValidationErrors() {
        // Given - Create an invalid course request
        CourseRequestDTO invalidRequest = new CourseRequestDTO(
                "", // Invalid: empty name
                "InvalidCategory", // Invalid: not in enum
                List.of() // Invalid: empty lessons
        );

        // When - Call the API
        String url = buildCourseUrl("");
        ResponseEntity<ProblemResponse> response = restTemplate.postForEntity(
                url, invalidRequest, ProblemResponse.class);

        // Then - Verify validation errors
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ProblemResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);

        assertEquals("Validation failed", errorResponse.detail());
        assertThat(errorResponse.errors()).isNotEmpty();

        // Verify specific error fields
        boolean hasNameError = errorResponse.errors().stream()
                .anyMatch(error -> error.field().contains("name"));
        boolean hasCategoryError = errorResponse.errors().stream()
                .anyMatch(error -> error.field().contains("category"));
        boolean hasLessonsError = errorResponse.errors().stream()
                .anyMatch(error -> error.field().contains("lessons"));

        assertTrue(hasNameError);
        assertTrue(hasCategoryError);
        assertTrue(hasLessonsError);
    }

    @Test
    @DisplayName("Should reject duplicate course names")
    void testCreateDuplicateCourse() {
        // Given - Create and save a course using repository
        createAndSaveCourseDirect("Duplicate Course", "Front-end");

        // Create another course with the same name
        CourseRequestDTO duplicateRequest = createValidCourseRequest("Duplicate Course", "Back-end");

        // When - Call the API
        String url = buildCourseUrl("");
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, duplicateRequest, String.class);

        // Then - Verify it's rejected
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        String errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("already exists"));
    }

    @Test
    @DisplayName("Should reject reusing the name of a soft-deleted course")
    void testCreateDuplicateOfDeletedCourse() {
        // Given - Create and soft delete a course
        Course course = createAndSaveCourseDirect("Deleted Course", "Front-end");
        courseRepository.delete(course);

        CourseRequestDTO duplicateRequest = createValidCourseRequest("Deleted Course", "Back-end");

        // When - Call the API
        ResponseEntity<String> response = restTemplate.postForEntity(
                buildCourseUrl(""), duplicateRequest, String.class);

        // Then - the unique constraint on name also covers soft-deleted rows,
        // so the API must reject the name with a friendly message
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        String errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("already exists"));
    }

    @Test
    @DisplayName("Should update an existing course")
    void testUpdateCourse() {
        // Given - Create and save a course using repository
        Course savedCourse = createAndSaveCourseDirect("Original Course", "Back-end");
        Long courseId = savedCourse.getId();

        CourseRequestDTO updateRequest = createValidCourseRequest("Updated Course", "Front-end");

        // When - Call the API
        String url = buildCourseUrl("/" + courseId);
        HttpEntity<CourseRequestDTO> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<CourseDTO> response = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, CourseDTO.class);

        // Then - Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CourseDTO updatedCourse = response.getBody();
        assertNotNull(updatedCourse);

        assertEquals("Updated Course", updatedCourse.name());
        assertEquals("Front-end", updatedCourse.category());

        // Verify it was actually updated in the database
        Course dbCourse = courseRepository.findById(courseId).orElse(null);
        assertNotNull(dbCourse);
        assertEquals("Updated Course", dbCourse.getName());
    }

    @Test
    @DisplayName("Should delete a course (soft delete)")
    void testDeleteCourse() {
        // Given - Create and save a course using repository
        Course savedCourse = createAndSaveCourseDirect("Course to Delete", "Back-end");
        Long courseId = savedCourse.getId();

        // When - Call the API
        String url = buildCourseUrl("/" + courseId);
        ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.DELETE, null, Void.class);

        // Then - Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify it was soft deleted (status changed to INACTIVE)
        Course deletedCourse = courseTestRepository.findByIdIgnoringRestriction(courseId).orElse(null);
        assertNotNull(deletedCourse);
        assertEquals(Status.INACTIVE, deletedCourse.getStatus());
    }

    @Test
    @DisplayName("Should find courses by name")
    void testFindCourseByName() {
        // Given - Save a course using repository
        createAndSaveCourseDirect("Findable Course", "Back-end");

        // When - Search by name
        String url = buildCourseUrl("/searchByName?name=Findable Course");
        ResponseEntity<CourseDTO[]> response = restTemplate.getForEntity(url, CourseDTO[].class);

        // Then - Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CourseDTO[] coursesArray = response.getBody();
        assertNotNull(coursesArray);
        assertThat(coursesArray).hasSize(1);

        CourseDTO foundCourse = coursesArray[0];
        assertEquals("Findable Course", foundCourse.name());
    }

    @Test
    @DisplayName("Should handle concurrent course creation")
    void testConcurrentCourseCreation() throws InterruptedException {
        // This test simulates concurrent requests to ensure data integrity
        // Given - Multiple course requests
        CourseRequestDTO request1 = createValidCourseRequest("Concurrent Course 1", "Back-end");
        CourseRequestDTO request2 = createValidCourseRequest("Concurrent Course 2", "Front-end");

        // When - Make concurrent requests
        Thread thread1 = new Thread(() -> {
            String url = buildCourseUrl("");
            restTemplate.postForEntity(url, request1, CourseDTO.class);
        });

        Thread thread2 = new Thread(() -> {
            String url = buildCourseUrl("");
            restTemplate.postForEntity(url, request2, CourseDTO.class);
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Then - Verify both courses were created
        List<Course> allCourses = courseRepository.findAll();
        // Note: May have test data from @Sql, so check for at least 2 new courses
        assertThat(allCourses).hasSizeGreaterThanOrEqualTo(2);
    }

    // Helper methods

    /**
     * Creates and saves a course directly to the database with automatic
     * transaction commit.
     * This ensures the data is visible to HTTP requests made via TestRestTemplate.
     */
    private Course createAndSaveCourseDirect(String name, String category) {
        Course course = new Course();
        course.setName(name);
        course.setCategory(com.loiane.course.enums.Category.valueOf(category.replace("-", "_").toUpperCase()));
        course.setStatus(Status.ACTIVE);

        // Add a lesson
        com.loiane.course.Lesson lesson = new com.loiane.course.Lesson();
        lesson.setName("Introduction to " + name);
        lesson.setYoutubeUrl(TEST_YOUTUBE_URL);
        lesson.setCourse(course);

        Set<com.loiane.course.Lesson> lessons = new HashSet<>();
        lessons.add(lesson);
        course.setLessons(lessons);

        // Save and flush to ensure data is committed
        return courseRepository.saveAndFlush(course);
    }

    private CourseRequestDTO createValidCourseRequest(String name, String category) {
        LessonDTO lesson = new LessonDTO(null, "Introduction", TEST_YOUTUBE_URL);
        return new CourseRequestDTO(name, category, List.of(lesson));
    }
}
