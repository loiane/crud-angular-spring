package com.loiane.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.mapper.CourseMapper;
import com.loiane.exception.RecordNotFoundException;
import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

import jakarta.validation.ConstraintViolationException;

@ActiveProfiles("test")
@ContextConfiguration(classes = { CourseService.class, CourseMapper.class })
@ExtendWith(SpringExtension.class)
class CourseServiceTest {

    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        ProxyFactory factory = new ProxyFactory(new CourseService(courseRepository, courseMapper));
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
     * Method under test: {@link CourseService#findById(Long)}
     * Happy path
     */
    @Test
    @DisplayName("Should return a course by id")
    void testFindById() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);
        Course actualFindByIdResult = this.courseService.findById(1L);
        assertSame(course, actualFindByIdResult);
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#findById(Long)}
     */
    @Test
    @DisplayName("Should thow NotFound exception when course not found")
    void testFindByIdNotFound() {
        when(this.courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class, () -> this.courseService.findById(123L));
        verify(this.courseRepository).findById(anyLong());
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
        CourseRequestDTO courseDTO = TestData.createValidCourseDTO();
        Course course = TestData.createValidCourse();
        when(this.courseRepository.save(any())).thenReturn(course);

        assertSame(course, this.courseService.create(courseDTO));
        verify(this.courseRepository).save((Course) any());
    }

    /**
     * Method under test: {@link CourseService#create(Course)}
     */
    @Test
    @DisplayName("Should throw an exception when creating an invalid course")
    void testCreateInvalid() {
        final List<CourseRequestDTO> courses = TestData.createInvalidCoursesDTO();
        for (CourseRequestDTO course : courses) {
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
        when(this.courseRepository.save(any())).thenReturn(course1);
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);

        Course course2 = Course.builder()
                .id(1L)
                .name("Spring Boot")
                .category("back-end")
                .build();
        assertSame(course1, this.courseService.update(123L, course2));
        verify(this.courseRepository).save(any());
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#update(Long, Course)}
     */
    @Test
    @DisplayName("Should throw an exception when updating an invalid course ID")
    void testUpdateNotFound() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        when(this.courseRepository.save(any())).thenThrow(new RecordNotFoundException(123L));
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);

        Course course1 = Course.builder()
                .id(1L)
                .name("Spring Boot")
                .category("back-end")
                .build();
        assertThrows(RecordNotFoundException.class, () -> this.courseService.update(123L, course1));
        verify(this.courseRepository).save(any());
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#update(Long, Course)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - update")
    void testUpdateInvalid() {

        Course validCourse = TestData.createValidCourse();

        // invalid id and valid course
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, validCourse));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, validCourse));

        // valid id and invalid course
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course : courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(1L, course));
        }

        // invalid id and invalid course
        for (Course course : courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, course));
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, course));
        }

        then(courseRepository).shouldHaveNoInteractions();
    }

    /**
     * Method under test: {@link CourseService#delete(Long)}
     */
    @Test
    @DisplayName("Should delete a course")
    void testDelete() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        doNothing().when(this.courseRepository).delete(any());
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);
        this.courseService.delete(1L);
        verify(this.courseRepository).findById(anyLong());
        verify(this.courseRepository).delete(any());
    }

    /**
     * Method under test: {@link CourseService#delete(Long)}
     */
    @Test
    @DisplayName("Should return empty when course not found - delete")
    void testDeleteNotFound() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        doThrow(new RecordNotFoundException(1L)).when(this.courseRepository).delete(any());
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);
        assertThrows(RecordNotFoundException.class, () -> this.courseService.delete(1L));
        verify(this.courseRepository).findById(anyLong());
        verify(this.courseRepository).delete((Course) any());
    }

    @Test
    @DisplayName("Should throw exception when id is not valid - delete")
    void testDeleteInvalid() {
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(-1L));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(null));
    }

}
