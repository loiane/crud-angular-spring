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
 * Validates that a collection contains at least one lesson with valid content.
 * This validator ensures not only that the collection is not empty,
 * but also that at least one lesson has meaningful content.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValidLessonCollectionValidator.class)
public @interface ValidLessonCollection {

    String message() default "Must contain at least one valid lesson";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
