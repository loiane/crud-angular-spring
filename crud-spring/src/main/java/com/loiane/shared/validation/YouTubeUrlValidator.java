package com.loiane.shared.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for YouTube video IDs.
 * Only the 11-character video ID is accepted (not full URLs), matching the
 * column length and the {@code @Length} constraints on the fields it is
 * applied to.
 */
public class YouTubeUrlValidator implements ConstraintValidator<ValidYouTubeUrl, String> {

    // YouTube video ID pattern: 11 characters, alphanumeric, hyphens, and
    // underscores
    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{11}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null and blank values are handled by @NotNull / @NotBlank if required
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return YOUTUBE_ID_PATTERN.matcher(value.trim()).matches();
    }
}
