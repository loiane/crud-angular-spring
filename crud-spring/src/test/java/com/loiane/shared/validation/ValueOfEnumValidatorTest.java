package com.loiane.shared.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loiane.course.enums.Category;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ValueOfEnumValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should accept values matching an enum constant")
    void testValidValues() {
        assertTrue(validator.validate(new TestClass("Front-end")).isEmpty());
        assertTrue(validator.validate(new TestClass("Back-end")).isEmpty());
        assertTrue(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    @DisplayName("Should reject values that do not match any enum constant")
    void testInvalidValues() {
        Set<ConstraintViolation<TestClass>> violations = validator.validate(new TestClass("Mobile"));
        assertFalse(violations.isEmpty());
    }

    // Test class for validation
    private static class TestClass {
        @ValueOfEnum(enumClass = Category.class)
        private final String category;

        public TestClass(String category) {
            this.category = category;
        }
    }
}
