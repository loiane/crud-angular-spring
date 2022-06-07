package com.loiane;

import java.util.List;

import com.loiane.model.Course;

public class TestData {
    
    private TestData() { }

    public static Course createValidCourse() {
        return Course.builder()
            .id(1L)
            .name("Spring")
            .category("back-end")
            .build();
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
            Course.builder().name(validName).category(validName).build()
        );
    }
}
