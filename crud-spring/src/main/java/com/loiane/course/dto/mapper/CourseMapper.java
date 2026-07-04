package com.loiane.course.dto.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loiane.course.Course;
import com.loiane.course.Lesson;
import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.dto.LessonDTO;
import com.loiane.course.enums.Category;

/**
 * Class to map the Course entity to the CourseRequestDTO and vice-versa.
 * ModelMapper currently does not support record types.
 */
@Component
public class CourseMapper {

    public Course toModel(CourseRequestDTO courseRequestDTO) {

        Course course = new Course();
        course.setName(courseRequestDTO.name());
        course.setCategory(convertCategoryValue(courseRequestDTO.category()));

        // ids sent by the client are ignored: every lesson is created as new
        Set<Lesson> lessons = courseRequestDTO.lessons().stream()
                .map(this::convertLessonDTOToLesson)
                .collect(Collectors.toSet());
        // setLessons wires the lesson -> course back-reference
        course.setLessons(lessons);

        return course;
    }

    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }
        List<LessonDTO> lessonDTOList = course.getLessons()
                .stream()
                .map(lesson -> new LessonDTO(lesson.getId(), lesson.getName(), lesson.getYoutubeUrl()))
                .toList();
        return new CourseDTO(course.getId(), course.getName(), course.getCategory().getValue(),
                lessonDTOList);
    }

    public Category convertCategoryValue(String value) {
        if (value == null) {
            return null;
        }
        return Category.fromValue(value);
    }

    /**
     * Creates a new (unsaved) Lesson from the DTO. The DTO id is not copied:
     * the database assigns ids, and client-supplied ids must not be trusted.
     */
    public Lesson convertLessonDTOToLesson(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setName(lessonDTO.name());
        lesson.setYoutubeUrl(lessonDTO.youtubeUrl());
        return lesson;
    }

}
