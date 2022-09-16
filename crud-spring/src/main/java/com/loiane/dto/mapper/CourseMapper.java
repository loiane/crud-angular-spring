package com.loiane.dto.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.LessonDTO;
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

        Course course = Course.builder().name(courseRequestDTO.name()).category(courseRequestDTO.category()).build();

        Set<Lesson> lessons = courseRequestDTO.lessons().stream()
                .map(lessonDTO -> Lesson.builder().id(lessonDTO._id()).name(lessonDTO.name())
                        .youtubeUrl(lessonDTO.youtubeUrl()).course(course).build())
                .collect(Collectors.toSet());
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
        return new CourseDTO(course.getId(), course.getName(), course.getCategory(), lessonDTOList);
    }
}
