package com.loiane.course.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Used as response and request object that represents a Lesson.
 */
public record LessonDTO(
                int _id,
                @NotBlank @NotNull @Length(min = 5, max = 100) String name,
                @NotBlank @NotNull @Length(min = 10, max = 11) String youtubeUrl) {
}
