package com.loiane;

import java.util.List;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.CourseRequestDTO;
import com.loiane.model.Course;

public class TestData {

    private static final String COURSE_NAME = "Spring";
    private static final String COURSE_CATEGORY = "back-end";
    private static final String LOREN_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et quam nec diam tristique mollis eget quis urna. Sed dapibus lectus in arcu rutrum, non luctus sem finibus. Cras nisl neque, pellentesque et tortor id, dapibus auctor turpis.";;

    private TestData() {
    }

    public static Course createValidCourse() {
        return Course.builder()
                .id(1L)
                .name("Spring")
                .category("back-end")
                .build();
    }

    public static CourseDTO createValidCourseDTO() {
        return new CourseDTO(1L, COURSE_NAME, COURSE_CATEGORY);
    }

    public static CourseRequestDTO createValidCourseRequest() {
        return new CourseRequestDTO(COURSE_NAME, COURSE_CATEGORY);
    }

    public static List<Course> createInvalidCourses() {
        final String validName = COURSE_NAME;
        final String validCategory = COURSE_CATEGORY;
        final String empty = "";

        return List.of(
                Course.builder().name(null).category(null).build(),
                Course.builder().name(null).category(validCategory).build(),
                Course.builder().name(empty).category(validCategory).build(),
                Course.builder().name("Spr").category(validCategory).build(),
                Course.builder().name(LOREN_IPSUM).category(validCategory).build(),
                Course.builder().name(validName).category(null).build(),
                Course.builder().name(validName).category(empty).build(),
                Course.builder().name(validName).category(LOREN_IPSUM).build(),
                Course.builder().name(validName).category(validName).build());
    }

    public static List<CourseRequestDTO> createInvalidCoursesDTO() {
        final String validName = COURSE_NAME;
        final String validCategory = COURSE_CATEGORY;
        final String empty = "";

        return List.of(
                new CourseRequestDTO(null, null),
                new CourseRequestDTO(validCategory, null),
                new CourseRequestDTO(validCategory, empty),
                new CourseRequestDTO(validCategory, "Spr"),
                new CourseRequestDTO(validCategory, LOREN_IPSUM),
                new CourseRequestDTO(null, validName),
                new CourseRequestDTO(empty, validName),
                new CourseRequestDTO(LOREN_IPSUM, validName),
                new CourseRequestDTO(validCategory, validName));
    }
}
