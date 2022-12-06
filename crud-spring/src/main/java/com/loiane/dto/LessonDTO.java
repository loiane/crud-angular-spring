package com.loiane.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Used as response and request object that represents a Lesson.
 */
public record LessonDTO(
        @Positive int _id,
        @NotBlank @NotNull @Length(min = 5, max = 100) String name,
        @NotBlank @NotNull @Length(min = 10, max = 11) String youtubeUrl) {
}
