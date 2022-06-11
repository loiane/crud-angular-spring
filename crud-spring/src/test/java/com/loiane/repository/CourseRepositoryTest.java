package com.loiane.repository;

import java.util.List;
import java.util.Optional;

import com.loiane.TestData;
import com.loiane.model.Course;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This is a sample class to test the CourseRepository.
 * In practice, only additional methods to the interface should be tested.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class) 
@DataJpaTest
class CourseRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    CourseRepository courseRepository;

    @Test
    @DisplayName("Should find all courses in the database")
    void testFindAll() {
        Course course = Course.builder().name("Spring").category("back-end").build();
        entityManager.persist(course);

        var courses = courseRepository.findAll();

        assertThat(courses).isNotEmpty();
    }

    @Test
    @DisplayName("Should find a course by id")
    void testFindById() {
        Course course = Course.builder().name("Spring").category("back-end").build();
        entityManager.persist(course);

        Optional<Course> courseFound = courseRepository.findById(course.getId());

        assertThat(courseFound).isPresent();
    }

    @Test
    @DisplayName("Should not find a course that does not exist")
    void testFindByIdNotFound() {
        Optional<Course> courseFound = courseRepository.findById(100L);
        assertThat(courseFound).isNotPresent();
    }

    @Test
    @DisplayName("Should save a course when record is valid")
    void testSave() {
        Course course = TestData.createValidCourse();
        final Course courseSaved = courseRepository.save(course);

        final Course actual = entityManager.find(Course.class, courseSaved.getId());

        assertThat(courseSaved.getId()).isPositive();
        assertThat(actual).isEqualTo(courseSaved);
    }

    @Test
    @DisplayName("Should throw an exception when creating an invalid course")
    void testCreateInvalid() {
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course: courses) {
            assertThrows(ConstraintViolationException.class, () -> courseRepository.save(course));
        } 
    }
}
