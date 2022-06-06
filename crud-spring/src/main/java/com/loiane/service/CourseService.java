package com.loiane.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Validated
public class CourseService {
    
    private final CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(@Positive @NotNull Long id) {
        return courseRepository.findById(id);
    }

    public Course create(@Valid Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> update(@Positive @NotNull Long id, @Valid Course course) {
        return courseRepository.findById(id).map(actual -> {
            actual.setName(course.getName());
            actual.setCategory(course.getCategory());
            Course updated = courseRepository.save(actual);
            return Optional.of(updated);
        })
        .orElse(Optional.empty());
    }

    public Optional<Boolean> delete(@Positive @NotNull Long id) {
        return courseRepository.findById(id)
        .map(
            course -> {
            courseRepository.deleteById(id);
            return Optional.of(true);
        })
        .orElse(Optional.empty());
    }
}
