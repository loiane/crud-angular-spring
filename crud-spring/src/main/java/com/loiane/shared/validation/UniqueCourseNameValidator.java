package com.loiane.shared.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.loiane.course.Course;
import com.loiane.course.CourseRepository;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.enums.Status;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for unique course names among active courses.
 * This validator checks if a course name is already in use by another active
 * course.
 */
@Component
public class UniqueCourseNameValidator implements ConstraintValidator<UniqueCourseNameValidation, CourseRequestDTO> {

    private CourseRepository courseRepository;

    // No-argument constructor required by Hibernate Validator
    public UniqueCourseNameValidator() {
    }

    // Constructor injection for Spring context
    @Autowired
    public UniqueCourseNameValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Set the repository via Spring after instantiation
    @Autowired
    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void initialize(UniqueCourseNameValidation constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(CourseRequestDTO courseRequestDTO, ConstraintValidatorContext context) {
        // If repository is not injected, return true (let other validation handle it)
        if (courseRepository == null) {
            return true;
        }

        // Find courses with the same name
        List<Course> existingCourses = courseRepository.findByName(courseRequestDTO.name());

        // Check if any active course has the same name
        boolean duplicateExists = existingCourses.stream()
                .anyMatch(course -> course.getStatus().equals(Status.ACTIVE));

        if (duplicateExists) {
            // Add custom error message to the name field
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "A course with name '" + courseRequestDTO.name() + "' already exists")
                    .addPropertyNode("name")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
