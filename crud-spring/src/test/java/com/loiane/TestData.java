package com.loiane;

import java.util.List;

import com.loiane.dto.CourseRequestDTO;
import com.loiane.model.Course;

public class TestData {

    private TestData() {
    }

    public static Course createValidCourse() {
        return Course.builder()
                .id(1L)
                .name("Spring")
                .category("back-end")
                .build();
    }

    public static CourseRequestDTO createValidCourseDTO() {
        return new CourseRequestDTO("Spring", "back-end");
    }

    public static List<Course> createInvalidCourses() {
        final String validName = "Spring";
        final String validCategory = "back-end";
        final String empty = "";
        final String lorenIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et quam nec diam tristique mollis eget quis urna. Sed dapibus lectus in arcu rutrum, non luctus sem finibus. Cras nisl neque, pellentesque et tortor id, dapibus auctor turpis.";

        return List.of(
                Course.builder().name(null).category(null).build(),
                Course.builder().name(null).category(validCategory).build(),
                Course.builder().name(empty).category(validCategory).build(),
                Course.builder().name("Spr").category(validCategory).build(),
                Course.builder().name(lorenIpsum).category(validCategory).build(),
                Course.builder().name(validName).category(null).build(),
                Course.builder().name(validName).category(empty).build(),
                Course.builder().name(validName).category(lorenIpsum).build(),
                Course.builder().name(validName).category(validName).build());
    }

    public static List<CourseRequestDTO> createInvalidCoursesDTO() {
        final String validName = "Spring";
        final String validCategory = "back-end";
        final String empty = "";
        final String lorenIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et quam nec diam tristique mollis eget quis urna. Sed dapibus lectus in arcu rutrum, non luctus sem finibus. Cras nisl neque, pellentesque et tortor id, dapibus auctor turpis.";

        return List.of(
                new CourseRequestDTO(null, null),
                new CourseRequestDTO(validCategory, null),
                new CourseRequestDTO(validCategory, empty),
                new CourseRequestDTO(validCategory, "Spr"),
                new CourseRequestDTO(validCategory, lorenIpsum),
                new CourseRequestDTO(null, validName),
                new CourseRequestDTO(empty, validName),
                new CourseRequestDTO(lorenIpsum, validName),
                new CourseRequestDTO(validCategory, validName));
    }
}
