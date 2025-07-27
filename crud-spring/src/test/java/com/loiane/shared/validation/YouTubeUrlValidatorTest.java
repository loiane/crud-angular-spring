package com.loiane.shared.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;

class YouTubeUrlValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept valid YouTube video IDs")
    void testValidYouTubeIds() {
        TestClass testObj = new TestClass("dQw4w9WgXcQ");
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());

        testObj = new TestClass("Fj3Zvf-N4bk");
        violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should extract and validate YouTube URLs")
    void testYouTubeUrls() {
        TestClass testObj = new TestClass("https://youtu.be/dQw4w9WgXcQ");
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());

        testObj = new TestClass("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        violations = validator.validate(testObj);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid YouTube IDs")
    void testInvalidYouTubeIds() {
        TestClass testObj = new TestClass("too_short");
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testObj);
        assertFalse(violations.isEmpty());

        testObj = new TestClass("way_too_long_id");
        violations = validator.validate(testObj);
        assertFalse(violations.isEmpty());

        testObj = new TestClass("invalid@chars");
        violations = validator.validate(testObj);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept null values")
    void testNullValues() {
        TestClass testObj = new TestClass(null);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testObj);
        // Should have violation for @NotNull but not for @ValidYouTubeUrl
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("url")
                && v.getConstraintDescriptor().getAnnotation().annotationType().equals(NotNull.class)));
    }

    // Test class for validation
    private static class TestClass {
        @NotNull
        @ValidYouTubeUrl
        private final String url;

        public TestClass(String url) {
            this.url = url;
        }
    }
}
