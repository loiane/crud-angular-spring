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
 * Validates that a string represents a valid YouTube video ID format.
 * YouTube video IDs are typically 11 characters long and contain alphanumeric
 * characters,
 * hyphens, and underscores.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = YouTubeUrlValidator.class)
public @interface ValidYouTubeUrl {

    String message() default "Invalid YouTube URL format. Must be a valid YouTube video ID (11 characters)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
