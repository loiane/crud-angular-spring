package com.loiane.shared.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class ValidCourseNameValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<ConstraintViolation<TestClass>> validate(String name) {
        return validator.validate(new TestClass(name));
    }

    private String firstMessage(Set<ConstraintViolation<TestClass>> violations) {
        return violations.iterator().next().getMessage();
    }

    @Test
    @DisplayName("Should accept valid course names")
    void testValidNames() {
        assertTrue(validate("Spring Boot Fundamentals").isEmpty());
        assertTrue(validate("Angular v22 (Signals & Forms)").isEmpty());
        assertTrue(validate(null).isEmpty());
    }

    @Test
    @DisplayName("Should reject names with invalid characters")
    void testInvalidCharacters() {
        Set<ConstraintViolation<TestClass>> violations = validate("Spring @ Home!");
        assertFalse(violations.isEmpty());
        assertEquals("Course name contains invalid characters", firstMessage(violations));
    }

    @Test
    @DisplayName("Should reject names with excessive consecutive special characters")
    void testExcessiveSpecialChars() {
        Set<ConstraintViolation<TestClass>> violations = validate("Spring ---- Boot");
        assertFalse(violations.isEmpty());
        assertEquals("Course name contains too many consecutive special characters", firstMessage(violations));
    }

    @Test
    @DisplayName("Should reject names with excessive spaces")
    void testExcessiveSpaces() {
        Set<ConstraintViolation<TestClass>> violations = validate("Spring    Boot");
        assertFalse(violations.isEmpty());
        assertEquals("Course name contains excessive spaces", firstMessage(violations));
    }

    @Test
    @DisplayName("Should reject names that start or end with spaces")
    void testLeadingOrTrailingSpaces() {
        Set<ConstraintViolation<TestClass>> violations = validate(" Spring Boot");
        assertFalse(violations.isEmpty());
        assertEquals("Course name cannot start or end with spaces", firstMessage(violations));
    }

    @Test
    @DisplayName("Should reject placeholder or spam-like words")
    void testInappropriateWords() {
        Set<ConstraintViolation<TestClass>> violations = validate("Lorem Ipsum Course");
        assertFalse(violations.isEmpty());
        assertEquals("Course name appears to contain test or placeholder content", firstMessage(violations));

        assertFalse(validate("qwerty basics").isEmpty());
    }

    @Test
    @DisplayName("Should not reject real names containing placeholder substrings")
    void testWholeWordMatchingOnly() {
        assertTrue(validate("Latest Angular Features").isEmpty());
    }

    @Test
    @DisplayName("Should reject names that are only numbers")
    void testOnlyNumbers() {
        Set<ConstraintViolation<TestClass>> violations = validate("12345");
        assertFalse(violations.isEmpty());
        assertEquals("Course name must contain meaningful text, not just numbers", firstMessage(violations));
    }

    // Test class for validation
    private static class TestClass {
        @ValidCourseName
        private final String name;

        public TestClass(String name) {
            this.name = name;
        }
    }
}
