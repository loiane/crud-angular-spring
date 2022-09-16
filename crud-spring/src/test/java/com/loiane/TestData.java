package com.loiane;

import java.util.List;
import java.util.Set;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.LessonDTO;
import com.loiane.model.Course;
import com.loiane.model.Lesson;

public class TestData {

    private static final String COURSE_NAME = "Spring";
    private static final String COURSE_CATEGORY = "back-end";
    private static final String LOREN_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et quam nec diam tristique mollis eget quis urna. Sed dapibus lectus in arcu rutrum, non luctus sem finibus. Cras nisl neque, pellentesque et tortor id, dapibus auctor turpis.";

    private static final String LESSON_NAME = "Spring Intro";
    private static final String LESSON_YOUTUBE = "abcdefgh123";

    private TestData() {
    }

    public static Course createValidCourse() {
        Course course = Course.builder()
                .id(1L)
                .name("Spring")
                .category("back-end")
                .build();
        course.setLessons(
                Set.of(Lesson.builder().id(1).name("Intro").youtubeUrl("abcdefgh123").course(course).build()));
        return course;
    }

    public static CourseDTO createValidCourseDTO() {
        return new CourseDTO(1L, COURSE_NAME, COURSE_CATEGORY, List.of(new LessonDTO(1, LESSON_NAME, LESSON_YOUTUBE)));
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
