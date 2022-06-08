package com.loiane.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.loiane.TestData;
import com.loiane.ValidationAdvice;
import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

import jakarta.validation.ConstraintViolationException;

@ActiveProfiles("test")
@ContextConfiguration(classes = {CourseService.class})
@ExtendWith(SpringExtension.class)
public class CourseServiceTest {
    
    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        ProxyFactory factory = new ProxyFactory(new CourseService(courseRepository));
        factory.addAdvice(new ValidationAdvice());
        courseService = (CourseService) factory.getProxy();
    }

    /**
     * Method under test: {@link CourseService#findAll()}
     */
    @Test
    @DisplayName("Should return a list of courses")
    void testFindAll() {
        List<Course> courseList = List.of(TestData.createValidCourse());
        when(this.courseRepository.findAll()).thenReturn(courseList);
        List<Course> actualFindAllResult = this.courseService.findAll();
        assertSame(courseList, actualFindAllResult);
        assertFalse(actualFindAllResult.isEmpty());
        assertEquals(1, actualFindAllResult.size());
        verify(this.courseRepository).findAll();
    }

    /**
     * Method under test: {@link CourseService#findById(int)}
     * Happy path
     */
    @Test
    @DisplayName("Should return a course by id")
    void testFindById() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        when(this.courseRepository.findById((Long) any())).thenReturn(ofResult);
        Optional<Course> actualFindByIdResult = this.courseService.findById(1L);
        assertSame(ofResult, actualFindByIdResult);
        assertTrue(actualFindByIdResult.isPresent());
        verify(this.courseRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link CourseService#findById(int)}
     */
    @Test
    @DisplayName("Should return empty when course not found")
    void testFindByIdNotFound() {
        when(this.courseRepository.findById((Long) any())).thenReturn(Optional.empty());
        final Optional<Course> actualFindByIdResult = this.courseService.findById(1L);
        assertTrue(actualFindByIdResult.isEmpty());
        verify(this.courseRepository).findById((Long) any());
    }

    @Test
    @DisplayName("Should throw exception when id is not valid - findById")
    void testFindByIdInvalid() {
        assertThrows(ConstraintViolationException.class, () -> this.courseService.findById(-1L));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.findById(null));
    }

    /**
     * Method under test: {@link CourseService#create(Course)}
     */
    @Test
    @DisplayName("Should create a course when valid")
    void testCreate() {
        Course course = TestData.createValidCourse();
        when(this.courseRepository.save((Course) any())).thenReturn(course);

        Course course1 = TestData.createValidCourse();
        assertSame(course, this.courseService.create(course1));
        verify(this.courseRepository).save((Course) any());
    }

    /**
     * Method under test: {@link CourseService#create(Course)}
     */
    @Test
    @DisplayName("Should throw an exception when creating an invalid course")
    void createInvalid() {
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course: courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.create(course));
        } 
        then(courseRepository).shouldHaveNoInteractions();
    }

    /**
     * Method under test: {@link CourseService#update(Long, Course)}
     */
    @Test
    @DisplayName("Should update a course when valid")
    void testUpdate() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);

        Course course1 = TestData.createValidCourse();
        when(this.courseRepository.save((Course) any())).thenReturn(course1);
        when(this.courseRepository.findById((Long) any())).thenReturn(ofResult);

        Course course2 = Course.builder()
        .id(1L)
        .name("Spring Boot")
        .category("back-end")
        .build();
        assertTrue(this.courseService.update(1L, course2).isPresent());
        verify(this.courseRepository).save((Course) any());
        verify(this.courseRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link CourseService#update(Long, Course)}
     */
    @Test
    @DisplayName("Should update a course when valid v2")
    void testUpdate2() {
        Course course = TestData.createValidCourse();
        when(this.courseRepository.save((Course) any())).thenReturn(course);
        Optional<Course> emptyResult = Optional.empty();
        when(this.courseRepository.findById((Long) any())).thenReturn(emptyResult);

        Course course1 = Course.builder()
        .id(1L)
        .name("Spring Boot")
        .category("back-end")
        .build();
        Optional<Course> actualUpdateResult = this.courseService.update(1L, course1);
        assertSame(emptyResult, actualUpdateResult);
        assertFalse(actualUpdateResult.isPresent());
        verify(this.courseRepository).findById((Long) any());
    }

    @Test
    @DisplayName("Should throw exception when id is not valid - update")
    void testUpdateInvalid() {

        Course validCourse = TestData.createValidCourse();

        // invalid id and valid course
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, validCourse));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, validCourse));

        // valid id and invalid course
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course: courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(1L, course));
        }

        // invalid id and invalid course
        for (Course course: courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, course));
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, course));
        }

        then(courseRepository).shouldHaveNoInteractions();
    }

    /**
     * Method under test: {@link CourseService#delete(int)}
     */
    @Test
    @DisplayName("Should delete a course")
    void testDelete() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        doNothing().when(this.courseRepository).deleteById((Long) any());
        when(this.courseRepository.findById((Long) any())).thenReturn(ofResult);
        Optional<Boolean> actualDeleteResult = this.courseService.delete(1L);
        assertTrue(actualDeleteResult.isPresent());
        assertTrue(actualDeleteResult.get());
        verify(this.courseRepository).findById((Long) any());
        verify(this.courseRepository).deleteById((Long) any());
    }

    /**
     * Method under test: {@link CourseService#delete(int)}
     */
    @Test
    @DisplayName("Should return empty when course not found - delete")
    void testDelete2() {
        doNothing().when(this.courseRepository).deleteById((Long) any());
        Optional<Course> emptyResult = Optional.empty();
        when(this.courseRepository.findById((Long) any())).thenReturn(emptyResult);
        Optional<Boolean> actualDeleteResult = this.courseService.delete(1L);
        assertSame(emptyResult, actualDeleteResult);
        assertFalse(actualDeleteResult.isPresent());
        verify(this.courseRepository).findById((Long) any());
    }

    @Test
    @DisplayName("Should throw exception when id is not valid - delete")
    void testDeleteInvalid() {
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(-1L));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(null));
    }

}
