package com.loiane.shared.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for YouTube video URLs/IDs.
 * Validates that the provided string is a valid YouTube video ID format.
 */
public class YouTubeUrlValidator implements ConstraintValidator<ValidYouTubeUrl, String> {

    // YouTube video ID pattern: 11 characters, alphanumeric, hyphens, and
    // underscores
    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{11}$");

    @Override
    public void initialize(ValidYouTubeUrl constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are handled by @NotNull if required
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // Remove common YouTube URL prefixes if present
        String videoId = extractVideoId(value);

        // Validate the video ID format
        return YOUTUBE_ID_PATTERN.matcher(videoId).matches();
    }

    /**
     * Extracts the video ID from various YouTube URL formats
     * Supports:
     * - Direct video ID: "dQw4w9WgXcQ"
     * - YouTube short URL: "https://youtu.be/dQw4w9WgXcQ"
     * - YouTube full URL: "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
     */
    private String extractVideoId(String input) {
        String trimmed = input.trim();

        // If it's already just an ID (11 chars), return as is
        if (trimmed.length() == 11 && YOUTUBE_ID_PATTERN.matcher(trimmed).matches()) {
            return trimmed;
        }

        // Extract from youtu.be short URL
        if (trimmed.contains("youtu.be/")) {
            int startIndex = trimmed.indexOf("youtu.be/") + 9;
            return extractIdFromIndex(trimmed, startIndex);
        }

        // Extract from youtube.com URL with v= parameter
        if (trimmed.contains("youtube.com/watch") && trimmed.contains("v=")) {
            int startIndex = trimmed.indexOf("v=") + 2;
            return extractIdFromIndex(trimmed, startIndex);
        }

        // Return the original input if no URL pattern is found
        return trimmed;
    }

    private String extractIdFromIndex(String url, int startIndex) {
        if (startIndex >= url.length()) {
            return url;
        }

        String remainder = url.substring(startIndex);

        // Find the end of the video ID (first occurrence of & or ? or end of string)
        int endIndex = remainder.length();
        for (char delimiter : new char[] { '&', '?', '#' }) {
            int delimiterIndex = remainder.indexOf(delimiter);
            if (delimiterIndex != -1 && delimiterIndex < endIndex) {
                endIndex = delimiterIndex;
            }
        }

        return remainder.substring(0, endIndex);
    }
}
