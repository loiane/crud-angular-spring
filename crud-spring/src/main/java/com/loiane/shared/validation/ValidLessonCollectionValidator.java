package com.loiane.shared.validation;

import java.util.List;

import com.loiane.course.dto.LessonDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for lesson collections to ensure quality content.
 * Validates that the collection contains at least one lesson with meaningful
 * content.
 */
public class ValidLessonCollectionValidator implements ConstraintValidator<ValidLessonCollection, List<LessonDTO>> {

    private static final int LESSON_NAME_MIN_LENGTH = 5;

    private static final int MIN_YOUTUBE_URL_LENGTH = 10;

    @Override
    public void initialize(ValidLessonCollection constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(List<LessonDTO> lessons, ConstraintValidatorContext context) {
        // Null or empty collections are handled by @NotEmpty and @NotNull
        if (lessons == null || lessons.isEmpty()) {
            return false;
        }

        // Check if at least one lesson has valid content
        boolean hasValidLesson = lessons.stream()
                .anyMatch(this::isValidLesson);

        if (!hasValidLesson) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "At least one lesson must have a valid name and YouTube URL")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Checks if a lesson has valid content
     */
    private boolean isValidLesson(LessonDTO lesson) {
        return lesson != null
                && lesson.name().trim().length() >= LESSON_NAME_MIN_LENGTH
                && !lesson.name().trim().isEmpty()
                && lesson.name().trim().length() >= 5
                && lesson.youtubeUrl() != null
                && !lesson.youtubeUrl().trim().isEmpty()
                && lesson.youtubeUrl().trim().length() >= MIN_YOUTUBE_URL_LENGTH;
    }
}
