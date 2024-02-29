package com.loiane.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.loiane.config.ValidationAdvice;
import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CoursePageDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.dto.mapper.CourseMapper;
import com.loiane.exception.BusinessException;
import com.loiane.exception.RecordNotFoundException;

import jakarta.validation.ConstraintViolationException;

@SuppressWarnings("null")
@ActiveProfiles("test")
@SpringJUnitConfig(classes = { CourseService.class, CourseMapper.class })
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
     * Method under test: {@link CourseService#findAll(int, int)}
     */
    @Test
    @DisplayName("Should return a list of courses with pagination")
    void testFindAllPageable() {
        List<Course> courseList = List.of(TestData.createValidCourse());
        Page<Course> coursePage = new PageImpl<>(courseList);
        when(this.courseRepository.findAll(any(PageRequest.class))).thenReturn(coursePage);
        List<CourseDTO> dtoList = new ArrayList<>(courseList.size());
        for (Course course : courseList) {
            dtoList.add(courseMapper.toDTO(course));
        }

        CoursePageDTO coursePageDTO = this.courseService.findAll(0, 5);
        assertEquals(dtoList, coursePageDTO.courses());
        assertThat(coursePageDTO.courses()).isNotEmpty();
        assertEquals(1, coursePageDTO.totalElements());
        assertThat(coursePageDTO.courses().get(0).lessons()).isNotEmpty();
        verify(this.courseRepository).findAll(any(PageRequest.class));
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
        CourseDTO actualFindByIdResult = this.courseService.findById(1L);
        assertEquals(courseMapper.toDTO(ofResult.get()), actualFindByIdResult);
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#findById(Long)}
     */
    @Test
    @DisplayName("Should throw NotFound exception when course not found")
    void testFindByIdNotFound() {
        when(this.courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class, () -> this.courseService.findById(123L));
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#findById(Long)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - findById")
    void testFindByIdInvalid() {
        assertThrows(ConstraintViolationException.class, () -> this.courseService.findById(-1L));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.findById(null));
    }

    /**
     * Method under test: {@link CourseService#findByName(String)}
     */
    @Test
    @DisplayName("Should return a course by name")
    void testFindByName() {
        Course course = TestData.createValidCourse();
        when(this.courseRepository.findByName(anyString())).thenReturn(List.of(course));
        List<CourseDTO> listByName = this.courseService.findByName("Spring");
        assertThat(listByName).isNotEmpty();
        assertEquals(courseMapper.toDTO(course), listByName.get(0));
        verify(this.courseRepository).findByName(anyString());
    }

    /**
     * Method under test: {@link CourseService#create(CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should create a course when valid")
    void testCreate() {
        CourseRequestDTO courseDTO = TestData.createValidCourseRequest();
        Course course = TestData.createValidCourse();
        when(this.courseRepository.save(any())).thenReturn(course);

        assertEquals(courseMapper.toDTO(course), this.courseService.create(courseDTO));
        verify(this.courseRepository).save(any());
    }

    /**
     * Method under test: {@link CourseService#create(CourseRequestDTO)}
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
     * Method under test: {@link CourseService#create(CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should throw an exception when creating a duplicate course")
    void testCreateSameName() {
        CourseRequestDTO courseRequestDTO = TestData.createValidCourseRequest();
        when(this.courseRepository.findByName(any()))
                .thenReturn(List.of(TestData.createValidCourse()));

        assertThrows(BusinessException.class, () -> this.courseService.create(courseRequestDTO));
        verify(this.courseRepository).findByName(any());
        verify(this.courseRepository, times(0)).save(any());
    }

    /**
     * Method under test: {@link CourseService#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should update a course when valid")
    void testUpdate() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);

        Course course1 = TestData.createValidCourse();
        when(this.courseRepository.save(any())).thenReturn(course1);
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);

        CourseRequestDTO course2 = TestData.createValidCourseRequest();
        assertEquals(courseMapper.toDTO(course1), this.courseService.update(1L, course2));
        verify(this.courseRepository).save(any());
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should throw an exception when updating an invalid course ID")
    void testUpdateNotFound() {
        Course course = TestData.createValidCourse();
        Optional<Course> ofResult = Optional.of(course);
        when(this.courseRepository.save(any())).thenThrow(new RecordNotFoundException(123L));
        when(this.courseRepository.findById(anyLong())).thenReturn(ofResult);

        CourseRequestDTO course1 = TestData.createValidCourseRequest();
        assertThrows(RecordNotFoundException.class, () -> this.courseService.update(123L, course1));
        verify(this.courseRepository).save(any());
        verify(this.courseRepository).findById(anyLong());
    }

    /**
     * Method under test: {@link CourseService#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - update")
    void testUpdateInvalid() {

        CourseRequestDTO validCourse = TestData.createValidCourseRequest();

        // invalid id and valid course
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, validCourse));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, validCourse));

        // valid id and invalid course
        final List<CourseRequestDTO> courses = TestData.createInvalidCoursesDTO();
        for (CourseRequestDTO course : courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(1L, course));
        }

        // invalid id and invalid course
        for (CourseRequestDTO course : courses) {
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(-1L, course));
            assertThrows(ConstraintViolationException.class, () -> this.courseService.update(null, course));
        }

        then(courseRepository).shouldHaveNoInteractions();
    }

    /**
     * Method under test: {@link CourseService#delete(Long)}
     */
    @Test
    @DisplayName("Should soft delete a course")
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
        verify(this.courseRepository).delete(any());
    }

    /**
     * Method under test: {@link CourseService#delete(Long)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - delete")
    void testDeleteInvalid() {
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(-1L));
        assertThrows(ConstraintViolationException.class, () -> this.courseService.delete(null));
    }

}
