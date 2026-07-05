package com.loiane.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.loiane.config.TestContainersConfiguration;
import com.loiane.course.enums.Category;
import com.loiane.course.enums.Status;

/**
 * This is a sample class to test the CourseRepository.
 * In practice, only additional methods to the interface should be tested.
 */
@ActiveProfiles("integration-test")
@DataJpaTest
@Import(TestContainersConfiguration.class)
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    CourseRepository courseRepository;

    /**
     * Method under test:
     * {@link CourseRepository#findByNameContainingIgnoreCase(String, Limit)}
     */
    @Test
    @DisplayName("Should find courses by partial name ignoring case")
    void testFindByNameContainingIgnoreCase() {
        Course course = createValidCourse();
        entityManager.persist(course);

        List<Course> courseFound = courseRepository.findByNameContainingIgnoreCase("spr", Limit.of(50));

        assertThat(courseFound).isNotEmpty();
        assertThat(courseFound.get(0).getName()).isEqualTo(course.getName());
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

    /**
     * Method under test: {@link CourseRepository#findByNameIgnoringRestriction(String)}
     */
    @Test
    @DisplayName("Should find a course by exact name")
    void testFindByName() {
        Course course = createValidCourse();
        // flush so the native query, which bypasses the persistence context,
        // can see the new row
        entityManager.persistAndFlush(course);

        List<Course> courseFound = courseRepository.findByNameIgnoringRestriction(course.getName());

        assertThat(courseFound).isNotEmpty();
        assertThat(courseFound.get(0).getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(courseFound.get(0).getLessons()).isNotEmpty();
    }

    private Course createValidCourse() {
        Course course = new Course();
        course.setName("Spring");
        course.setCategory(Category.BACK_END);

        Lesson lesson = new Lesson();
        lesson.setName("Lesson 1");
        lesson.setYoutubeUrl("abcdefgh123");
        lesson.setCourse(course);
        course.setLessons(Set.of(lesson));

        return course;
    }
}
