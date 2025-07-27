package com.loiane.shared.validation;

import java.util.Set;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for course names to ensure professional and appropriate content.
 * Validates formatting, length, and content appropriateness.
 */
public class ValidCourseNameValidator implements ConstraintValidator<ValidCourseName, String> {

    // Pattern for valid course names: letters, numbers, spaces, and common
    // punctuation
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,()&+#]+$");

    // Patterns to detect potential issues
    private static final Pattern EXCESSIVE_SPECIAL_CHARS = Pattern.compile("[^a-zA-Z0-9\\s]{3,}");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s{3,}");
    private static final Pattern STARTS_OR_ENDS_WITH_SPACE = Pattern.compile("(^\\s)|(\\s$)");

    // Common inappropriate or spam-like words (extend as needed)
    private static final Set<String> INAPPROPRIATE_WORDS = Set.of(
            "test", "testing", "dummy", "sample", "lorem", "ipsum", "asdf", "qwerty");

    @Override
    public void initialize(ValidCourseName constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        String trimmedValue = value.trim();

        // Check basic format
        if (!VALID_NAME_PATTERN.matcher(trimmedValue).matches()) {
            addCustomMessage(context, "Course name contains invalid characters");
            return false;
        }

        // Check for excessive special characters
        if (EXCESSIVE_SPECIAL_CHARS.matcher(trimmedValue).find()) {
            addCustomMessage(context, "Course name contains too many consecutive special characters");
            return false;
        }

        // Check for excessive spaces
        if (MULTIPLE_SPACES.matcher(trimmedValue).find()) {
            addCustomMessage(context, "Course name contains excessive spaces");
            return false;
        }

        // Check if starts or ends with space (after trim this shouldn't happen, but
        // safety check)
        if (STARTS_OR_ENDS_WITH_SPACE.matcher(value).find()) {
            addCustomMessage(context, "Course name cannot start or end with spaces");
            return false;
        }

        // Check for inappropriate words
        String lowerCaseName = trimmedValue.toLowerCase();
        boolean containsInappropriate = INAPPROPRIATE_WORDS.stream()
                .anyMatch(word -> lowerCaseName.contains(word.toLowerCase()));

        if (containsInappropriate) {
            addCustomMessage(context, "Course name appears to contain test or placeholder content");
            return false;
        }

        // Check for minimal meaningful content (not just numbers or single words)
        String[] words = trimmedValue.split("\\s+");
        if (words.length == 1 && words[0].matches("\\d+")) {
            addCustomMessage(context, "Course name must contain meaningful text, not just numbers");
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
