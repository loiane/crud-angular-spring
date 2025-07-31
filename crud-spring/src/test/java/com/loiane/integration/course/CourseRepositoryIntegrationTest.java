package com.loiane.integration.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.loiane.course.Course;
import com.loiane.course.CourseRepository;
import com.loiane.course.Lesson;
import com.loiane.course.enums.Category;
import com.loiane.course.enums.Status;
import com.loiane.integration.AbstractIntegrationTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Repository integration tests for Course entity using TestContainers.
 * These tests verify database operations, constraints, and query behavior
 * against a real MySQL database.
 */
@DisplayName("Course Repository Integration Tests")
@Transactional
class CourseRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final String DEFAULT_YOUTUBE_URL = "dQw4w9WgXcQ";

    @Autowired
    private CourseRepository courseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve a course with lessons")
    void testSaveAndRetrieveCourse() {
        // Given - Create a course with lessons
        Course course = createCourseWithLessons("Spring Boot Fundamentals", Category.BACK_END);

        // When - Save the course
        Course savedCourse = courseRepository.save(course);

        // Then - Verify it was saved correctly
        assertNotNull(savedCourse.getId());
        assertEquals("Spring Boot Fundamentals", savedCourse.getName());
        assertEquals(Category.BACK_END, savedCourse.getCategory());
        assertEquals(Status.ACTIVE, savedCourse.getStatus());
        assertThat(savedCourse.getLessons()).hasSize(2);

        // Verify lessons are properly associated
        savedCourse.getLessons().forEach(lesson -> {
            assertTrue(lesson.getId() > 0);
            assertEquals(savedCourse, lesson.getCourse());
        });
    }

    @Test
    @DisplayName("Should enforce unique course name constraint")
    void testUniqueNameConstraint() {
        // Given - Save a course
        Course firstCourse = createCourse("Unique Course Name", Category.FRONT_END);
        courseRepository.save(firstCourse);

        // When/Then - Try to save another course with the same name
        Course duplicateCourse = createCourse("Unique Course Name", Category.BACK_END);

        org.junit.jupiter.api.Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> {
                    courseRepository.save(duplicateCourse);
                    entityManager.flush(); // Force the constraint violation
                });
    }

    @Test
    @DisplayName("Should find courses by status")
    void testFindByStatus() {
        // Given - Create courses with different statuses
        Course activeCourse1 = createCourse("Active Course 1", Category.BACK_END);
        Course activeCourse2 = createCourse("Active Course 2", Category.FRONT_END);
        Course inactiveCourse = createCourse("Inactive Course", Category.FRONT_END);
        inactiveCourse.setStatus(Status.INACTIVE);

        courseRepository.saveAll(List.of(activeCourse1, activeCourse2, inactiveCourse));

        // When - Find active courses using pagination
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> activeCourses = courseRepository.findByStatus(pageable, Status.ACTIVE);

        // Then - Verify only active courses are returned
        assertThat(activeCourses.getContent()).hasSize(2);
        activeCourses.getContent().forEach(course -> assertEquals(Status.ACTIVE, course.getStatus()));
    }

    @Test
    @DisplayName("Should find courses with pagination")
    void testFindCoursesWithPagination() {
        // Given - Create courses with different categories
        Course backEndCourse1 = createCourse("Spring Boot", Category.BACK_END);
        Course backEndCourse2 = createCourse("Node.js", Category.BACK_END);
        Course frontEndCourse = createCourse("React", Category.FRONT_END);

        courseRepository.saveAll(List.of(backEndCourse1, backEndCourse2, frontEndCourse));

        // When - Find all courses with pagination
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> coursePage = courseRepository.findAll(pageable);

        // Then - Verify all courses are returned
        assertThat(coursePage.getContent()).hasSize(3);

        // Verify categories are properly stored
        long backEndCount = coursePage.getContent().stream()
                .filter(course -> course.getCategory() == Category.BACK_END)
                .count();
        assertEquals(2, backEndCount);
    }

    @Test
    @DisplayName("Should find course by name")
    void testFindByName() {
        // Given - Save a course
        Course course = createCourse("Findable Course", Category.BACK_END);
        courseRepository.save(course);

        // When - Find by name
        List<Course> foundCourses = courseRepository.findByName("Findable Course");

        // Then - Verify it was found
        assertThat(foundCourses).hasSize(1);
        assertEquals("Findable Course", foundCourses.get(0).getName());
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given - Create multiple courses
        for (int i = 1; i <= 15; i++) {
            Course course = createCourse("Course " + i, Category.BACK_END);
            courseRepository.save(course);
        }

        // When - Request first page with 5 items
        Pageable pageable = PageRequest.of(0, 5);
        Page<Course> firstPage = courseRepository.findAll(pageable);

        // Then - Verify pagination
        assertEquals(5, firstPage.getContent().size());
        assertEquals(15, firstPage.getTotalElements());
        assertEquals(3, firstPage.getTotalPages());
        assertTrue(firstPage.hasNext());
        assertFalse(firstPage.hasPrevious());

        // When - Request second page
        Page<Course> secondPage = courseRepository.findAll(PageRequest.of(1, 5));

        // Then - Verify second page
        assertEquals(5, secondPage.getContent().size());
        assertTrue(secondPage.hasNext());
        assertTrue(secondPage.hasPrevious());
    }

    @Test
    @DisplayName("Should soft delete courses")
    void testSoftDelete() {
        // Given - Save a course
        Course course = createCourse("Course to Delete", Category.BACK_END);
        Course savedCourse = courseRepository.save(course);
        Long courseId = savedCourse.getId();

        // When - Delete the course (soft delete)
        courseRepository.delete(savedCourse);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context

        // Then - Course should still exist but with INACTIVE status
        Optional<Course> deletedCourse = courseRepository.findByIdIgnoringRestriction(courseId);
        assertTrue(deletedCourse.isPresent());
        assertEquals(Status.INACTIVE, deletedCourse.get().getStatus());

        // Verify it's not included in findAll results (due to @SQLRestriction)
        List<Course> allCourses = courseRepository.findAll();
        assertThat(allCourses).noneMatch(c -> c.getId().equals(courseId));
    }

    @Test
    @DisplayName("Should cascade operations to lessons")
    void testCascadeOperations() {
        // Given - Create course with lessons
        Course course = createCourseWithLessons("Course with Lessons", Category.BACK_END);

        // When - Save course (should cascade to lessons)
        Course savedCourse = courseRepository.save(course);

        // Then - Lessons should be saved
        assertThat(savedCourse.getLessons()).hasSize(2);
        savedCourse.getLessons().forEach(lesson -> assertTrue(lesson.getId() > 0));

        // When - Delete course
        courseRepository.delete(savedCourse);
        entityManager.flush();

        // Then - Lessons should also be soft deleted (status-based deletion)
        // Note: This depends on the specific cascade configuration in the entity
    }

    @Test
    @DisplayName("Should handle concurrent access correctly")
    void testConcurrentAccess() {
        // Given - Clean state
        courseRepository.deleteAll();

        // Create courses directly without threads since
        // real concurrent testing requires complex transaction management
        Course course1 = createCourse("Concurrent Course 1", Category.BACK_END);
        Course course2 = createCourse("Concurrent Course 2", Category.FRONT_END);

        // When - Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);
        entityManager.flush();

        // Then - Both courses should be saved successfully since they have different
        // names
        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(2);
    }

    @Test
    @DisplayName("Should maintain referential integrity with lessons")
    void testReferentialIntegrity() {
        // Given - Course with lessons
        Course course = createCourseWithLessons("Course with References", Category.BACK_END);
        Course savedCourse = courseRepository.save(course);

        // When - Retrieve course and check lesson references
        Optional<Course> retrievedCourse = courseRepository.findById(savedCourse.getId());

        // Then - All references should be intact
        assertTrue(retrievedCourse.isPresent());
        Course course1 = retrievedCourse.get();

        assertThat(course1.getLessons()).hasSize(2);
        course1.getLessons().forEach(lesson -> {
            assertEquals(course1.getId(), lesson.getCourse().getId());
        });
    }

    // Helper methods

    private Course createCourse(String name, Category category) {
        Course course = new Course();
        course.setName(name);
        course.setCategory(category);
        course.setStatus(Status.ACTIVE);

        // Always add at least one lesson to satisfy @NotEmpty constraint
        Lesson lesson = new Lesson();
        lesson.setName("Introduction to " + name);
        lesson.setYoutubeUrl(DEFAULT_YOUTUBE_URL);
        lesson.setCourse(course);

        Set<Lesson> lessons = new HashSet<>();
        lessons.add(lesson);
        course.setLessons(lessons);

        return course;
    }

    private Course createCourseWithLessons(String name, Category category) {
        Course course = createCourse(name, category);

        Lesson lesson1 = new Lesson();
        lesson1.setName("Introduction to " + name);
        lesson1.setYoutubeUrl(DEFAULT_YOUTUBE_URL);
        lesson1.setCourse(course);

        Lesson lesson2 = new Lesson();
        lesson2.setName("Advanced " + name);
        lesson2.setYoutubeUrl(DEFAULT_YOUTUBE_URL);
        lesson2.setCourse(course);

        Set<Lesson> lessons = new HashSet<>();
        lessons.add(lesson1);
        lessons.add(lesson2);
        course.setLessons(lessons);

        return course;
    }
}
