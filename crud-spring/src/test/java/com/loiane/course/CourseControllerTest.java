package com.loiane.course;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.config.ValidationAdvice;
import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CoursePageDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.exception.RecordNotFoundException;

import jakarta.servlet.ServletException;

@SuppressWarnings("null")
@ActiveProfiles("test")
@SpringJUnitConfig(classes = { CourseController.class })
class CourseControllerTest {

    private final static String API = "/api/courses";
    private final static String API_ID = "/api/courses/{id}";

    @Autowired
    private CourseController courseController;

    @MockBean
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        ProxyFactory factory = new ProxyFactory(new CourseController(courseService));
        factory.addAdvice(new ValidationAdvice());
        courseController = (CourseController) factory.getProxy();
    }

    /**
     * Method under test: {@link CourseController#findAll(int, int)}
     */
    @Test
    @DisplayName("Should return a list of courses in JSON format")
    void testFindAll() throws Exception {
        CourseDTO course = TestData.createValidCourseDTO();
        List<CourseDTO> courses = List.of(course);
        CoursePageDTO pageDTO = new CoursePageDTO(courses, 1L, 1);
        when(this.courseService.findAll(anyInt(), anyInt())).thenReturn(pageDTO);
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(MockMvcRequestBuilders.get(API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("courses", hasSize(courses.size())))
                .andExpect(jsonPath("totalElements", is(1)))
                .andExpect(jsonPath("courses[0]._id", is(course.id()), Long.class))
                .andExpect(jsonPath("courses[0].name", is(course.name())))
                .andExpect(jsonPath("courses[0].category", is(course.category())));
    }

    /**
     * Method under test: {@link CourseController#findById(Long)}
     */
    @Test
    @DisplayName("Should return a course by id")
    void testFindById() throws Exception {
        CourseDTO course = TestData.createValidCourseDTO();
        when(this.courseService.findById(anyLong())).thenReturn(course);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_ID, course.id());
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("_id", is(course.id()), Long.class))
                .andExpect(jsonPath("name", is(course.name())))
                .andExpect(jsonPath("category", is(course.category())));
    }

    /**
     * Method under test: {@link CourseController#findById(Long)}
     */
    @Test
    @DisplayName("Should return a 404 status code when course is not found")
    void testFindByIdNotFound() {
        when(this.courseService.findById(anyLong())).thenThrow(new RecordNotFoundException(1L));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_ID, 1);
        assertThrows(ServletException.class, () -> {
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isNotFound());
        });
    }

    /**
     * Method under test: {@link CourseController#findById(Long)}
     */
    @Test
    @DisplayName("Should return bad request status code when id is not a positive number")
    void testFindByIdNegative() {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_ID, -1);
        assertThrows(ServletException.class, () -> {
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isBadRequest());
        });
    }

    /**
     * Method under test: {@link CourseController#findByName(String)}
     */
    @Test
    @DisplayName("Should return a course by name")
    void testFindByName() throws Exception {
        CourseDTO course = TestData.createValidCourseDTO();
        List<CourseDTO> courses = List.of(course);
        when(this.courseService.findByName(anyString())).thenReturn(courses);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(API + "/searchByName")
                .param("name", course.name());
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(courses.size())))
                .andExpect(jsonPath("$[0]._id", is(course.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(course.name())))
                .andExpect(jsonPath("$[0].category", is(course.category())));
    }

    /**
     * Method under test: {@link CourseController#create(CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should create a course when valid")
    void testCreate() throws Exception {
        CourseRequestDTO courseDTO = TestData.createValidCourseRequest();
        CourseDTO course = TestData.createValidCourseDTO();
        when(this.courseService.create(courseDTO)).thenReturn(course);

        String content = (new ObjectMapper()).writeValueAsString(course);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("_id", is(course.id()), Long.class))
                .andExpect(jsonPath("name", is(course.name())))
                .andExpect(jsonPath("category", is(course.category())));
    }

    /**
     * Method under test: {@link CourseController#create(CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should return bad request when creating an invalid course")
    void testCreateInvalid() throws Exception {
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course : courses) {
            String content = (new ObjectMapper()).writeValueAsString(course);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content);
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isBadRequest());
        }
    }

    /**
     * Method under test: {@link CourseController#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should update a course when valid")
    void testUpdate() throws Exception {
        CourseDTO course = TestData.createValidCourseDTO();
        when(this.courseService.update(anyLong(), any())).thenReturn(course);

        String content = (new ObjectMapper()).writeValueAsString(course);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(API_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("_id", is(course.id()), Long.class))
                .andExpect(jsonPath("name", is(course.name())))
                .andExpect(jsonPath("category", is(course.category())));
    }

    /**
     * Method under test: {@link CourseController#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should throw an exception when updating an invalid course ID")
    void testUpdateNotFound() throws Exception {
        Course course = TestData.createValidCourse();
        when(this.courseService.update(anyLong(), any())).thenThrow(new RecordNotFoundException(1L));

        String content = (new ObjectMapper()).writeValueAsString(course);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(API_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.courseController).build();
        assertThrows(AssertionError.class, () -> {
            ResultActions actualPerformResult = mockMvc.perform(requestBuilder);
            actualPerformResult.andExpect(status().isNotFound());
        });
    }

    /**
     * Method under test: {@link CourseController#update(Long, CourseRequestDTO)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - update")
    void testUpdateInvalid() throws Exception {

        // invalid id and valid course
        final Course validCourse = TestData.createValidCourse();
        final String content = (new ObjectMapper()).writeValueAsString(validCourse);
        assertThrows(AssertionError.class, () -> {
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(API_ID, -1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content);
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isMethodNotAllowed());
        });

        // valid id and invalid course
        final List<Course> courses = TestData.createInvalidCourses();
        for (Course course : courses) {
            final String contentUpdate = (new ObjectMapper()).writeValueAsString(course);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(API_ID, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(contentUpdate);
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isBadRequest());
        }

        // invalid id and invalid course
        for (Course course : courses) {
            final String contentUpdate = (new ObjectMapper()).writeValueAsString(course);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(API_ID, -1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(contentUpdate);
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isBadRequest());
        }
    }

    /**
     * Method under test: {@link CourseController#delete(Long)}
     */
    @Test
    @DisplayName("Should delete a course")
    void testDelete() throws Exception {
        doNothing().when(this.courseService).delete(anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(API_ID, 1);
        MockMvcBuilders.standaloneSetup(this.courseController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link CourseController#delete(Long)}
     */
    @Test
    @DisplayName("Should return empty when course not found - delete")
    void testDeleteNotFound() {
        doThrow(new RecordNotFoundException(1L)).doNothing().when(this.courseService).delete(anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(API_ID, 1);
        assertThrows(ServletException.class, () -> {
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isNotFound());
        });
    }

    /**
     * Method under test: {@link CourseController#delete(Long)}
     */
    @Test
    @DisplayName("Should throw exception when id is not valid - delete")
    void testDeleteInvalid() {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(API_ID, -1);
        assertThrows(ServletException.class, () -> {
            ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.courseController)
                    .build()
                    .perform(requestBuilder);
            actualPerformResult.andExpect(status().isBadRequest());
        });
    }

}
