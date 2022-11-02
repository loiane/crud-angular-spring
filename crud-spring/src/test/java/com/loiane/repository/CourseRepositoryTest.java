package com.loiane.repository;

import java.util.List;
import java.util.Optional;

import com.loiane.TestData;
import com.loiane.enums.Category;
import com.loiane.enums.Status;
import com.loiane.model.Course;
import com.loiane.model.Lesson;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Course course = createValidCourse();
        entityManager.persist(course);

        var courses = courseRepository.findAll();

        assertThat(courses).isNotEmpty();
        assertThat(courses.get(0).getLessons()).isNotEmpty();
    }

    @Test
    @DisplayName("Should save a course when record is valid")
    void testSave() {
        Course course = createValidCourse();
        final Course courseSaved = courseRepository.save(course);

        final Course actual = entityManager.find(Course.class, courseSaved.getId());

        assertThat(courseSaved.getId()).isPositive();
        assertThat(courseSaved.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(actual).isEqualTo(courseSaved);
    }

    @Test
    @DisplayName("Should throw an exception when creating an invalid course")
    void testCreateInvalid() {
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course : courses) {
            assertThrows(ConstraintViolationException.class, () -> courseRepository.save(course));
        }
    }

    @Test
    @DisplayName("Should find a course by id and Status ACTIVE")
    void testFindByIdAndStatus() {
        Course course = createValidCourse();
        entityManager.persist(course);

        Optional<Course> courseFound = courseRepository.findByIdAndStatus(course.getId(), Status.ACTIVE);

        assertThat(courseFound).isPresent();
        assertThat(courseFound.get().getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(courseFound.get().getLessons()).isNotEmpty();
    }

    @Test
    @DisplayName("Should not find a course by id with Status INACTIVE")
    void testFindByIdAndStatusInactive() {
        Course course = createValidCourse();
        course.setStatus(Status.INACTIVE);
        entityManager.persist(course);

        Optional<Course> courseFound = courseRepository.findByIdAndStatus(course.getId(), Status.ACTIVE);

        assertThat(courseFound).isNotPresent();
    }

    @Test
    void testFindByStatus() {
        Course course = createValidCourse();
        entityManager.persist(course);
        Page<Course> coursePage = courseRepository.findByStatus(PageRequest.of(0, 5), Status.ACTIVE);

        assertThat(coursePage).isNotNull();
        assertThat(coursePage.getContent()).isNotEmpty();
        assertThat(coursePage.getContent().get(0).getLessons()).isNotEmpty();
        coursePage.getContent().stream().forEach(c -> {
            assertThat(c.getStatus()).isEqualTo(Status.ACTIVE);
            assertThat(c.getLessons()).isNotEmpty();
        });
    }

    private Course createValidCourse() {
        return Course.builder()
                .name("Spring")
                .category(Category.BACK_END)
                .status(Status.ACTIVE)
                .lessons(List.of(Lesson.builder().name("Intro").youtubeUrl("abcdefgh123").build()))
                .build();
    }
}
