package com.loiane.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.mapper.CourseMapper;
import com.loiane.exception.RecordNotFoundException;
import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(@Positive @NotNull Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(id));
    }

    public Course create(@Valid CourseRequestDTO courseRequestDTO) {
        return courseRepository.save(courseMapper.dtoToModel(courseRequestDTO));
    }

    public Course update(@Positive @NotNull Long id, @Valid Course course) {
        return courseRepository.findById(id).map(actual -> {
            actual.setName(course.getName());
            actual.setCategory(course.getCategory());
            return courseRepository.save(actual);
        })
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public void delete(@Positive @NotNull Long id) {
        courseRepository.delete(courseRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
