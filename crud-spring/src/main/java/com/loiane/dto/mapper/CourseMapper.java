package com.loiane.dto.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.LessonDTO;
import com.loiane.enums.Category;
import com.loiane.dto.CourseDTO;
import com.loiane.model.Course;
import com.loiane.model.Lesson;

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

        Set<Lesson> lessons = courseRequestDTO.lessons().stream()
                .map(lessonDTO -> {
                    Lesson lesson = new Lesson();
                    if (lesson.getId() > 0) {
                        lesson.setId(lessonDTO._id());
                    }
                    lesson.setName(lessonDTO.name());
                    lesson.setYoutubeUrl(lessonDTO.youtubeUrl());
                    lesson.setCourse(course);
                    return lesson;
                }).collect(Collectors.toSet());
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
                .collect(Collectors.toList());
        return new CourseDTO(course.getId(), course.getName(), course.getCategory().getValue(),
                lessonDTOList);
    }

    public Category convertCategoryValue(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "Front-end" -> Category.FRONT_END;
            case "Back-end" -> Category.BACK_END;
            default -> throw new IllegalArgumentException("Invalid Category.");
        };
    }
}
