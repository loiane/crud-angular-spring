package com.loiane.shared.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validates that a course name contains appropriate professional content.
 * Ensures the name doesn't contain inappropriate words or excessive special
 * characters.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValidCourseNameValidator.class)
public @interface ValidCourseName {

    String message() default "Course name contains inappropriate content or formatting";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
