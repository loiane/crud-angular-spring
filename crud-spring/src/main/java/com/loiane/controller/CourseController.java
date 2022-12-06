package com.loiane.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.CoursePageDTO;
import com.loiane.dto.CourseRequestDTO;
import com.loiane.service.CourseService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents the REST API for the Course resource.
 */
@Validated
@RestController
@RequestMapping("api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public CoursePageDTO findAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {
        if (courseService.isNameValid(name)) {
            return courseService.findAll(page, pageSize, name);
        }
        throw new IllegalArgumentException("Invalid name.");
    }

    @GetMapping("/{id}")
    public CourseDTO findById(@PathVariable @Positive @NotNull Long id) {
        return courseService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CourseDTO create(@RequestBody @Valid CourseRequestDTO course) {
        return courseService.create(course);
    }

    @PutMapping(value = "/{id}")
    public CourseDTO update(@PathVariable @Positive @NotNull Long id,
            @RequestBody @Valid CourseRequestDTO course) {
        return courseService.update(id, course);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive @NotNull Long id) {
        courseService.delete(id);
    }
}
