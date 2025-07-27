package com.loiane.shared.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validates that a course name is unique among active courses.
 * This is a class-level validator that can access both the course name and ID
 * to properly validate uniqueness during updates.
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueCourseNameValidator.class)
public @interface UniqueCourseNameValidation {

    String message() default "A course with this name already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
