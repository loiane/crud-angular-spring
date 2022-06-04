package com.loiane.repository;

import java.util.Optional;

import com.loiane.model.Course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a sample class to test the CourseRepository.
 * In practice, only additional methods to the interface should be tested.
 */
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
        Course course = createCourse();
        entityManager.persist(course);

        var courses = courseRepository.findAll();

        assertThat(courses).isNotEmpty();
    }

    @Test
    @DisplayName("Should find a course by id")
    void testFindById() {
        Course course = createCourse();
        entityManager.persist(course);

        Optional<Course> courseFound = courseRepository.findById(course.getId());

        assertThat(courseFound).isPresent();
    }

    @Test
    @DisplayName("Should not find a course that does not exist")
    void shouldNotFindCourseById() {
        
        Optional<Course> courseFound = courseRepository.findById(100L);

        assertThat(courseFound).isNotPresent();
    }

    @Test
    @DisplayName("Should save a course when record is valid")
    void testSave() {
        Course course = createCourse();
        final Course courseSaved = courseRepository.save(course);

        final Course actual = entityManager.find(Course.class, courseSaved.getId());

        assertThat(courseSaved.getId()).isPositive();
        assertThat(actual).isEqualTo(courseSaved);
    }

    private Course createCourse() {
        return Course.builder()
            .name("Spring Boot")
            .category("back-end")
            .build();
    }
}
