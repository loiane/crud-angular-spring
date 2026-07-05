package com.loiane.course.dto;

import org.hibernate.validator.constraints.Length;

import com.loiane.shared.validation.ValidYouTubeUrl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Used as response and request object that represents a Lesson.
 * On requests, a null or zero {@code _id} means a new lesson.
 */
public record LessonDTO(
        Long _id,
        @NotBlank @NotNull @Length(min = 5, max = 100) String name,
        @NotBlank @NotNull @Length(min = 10, max = 11) @ValidYouTubeUrl String youtubeUrl) {
}
