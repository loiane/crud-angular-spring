package com.loiane.shared.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;

import com.loiane.course.Course;
import com.loiane.exception.BusinessException;
import com.loiane.exception.RecordNotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;

class ApplicationControllerAdviceTest {

    private final ApplicationControllerAdvice advice = new ApplicationControllerAdvice();

    @Test
    @DisplayName("Should return 404 Problem Detail for RecordNotFoundException")
    void testHandleNotFoundException() {
        ProblemDetail detail = advice.handleNotFoundException(new RecordNotFoundException(1L));
        assertEquals(404, detail.getStatus());
    }

    @Test
    @DisplayName("Should return 409 Problem Detail for BusinessException")
    void testHandleBusinessException() {
        ProblemDetail detail = advice.handleBusinessException(new BusinessException("Duplicate course"));
        assertEquals(409, detail.getStatus());
        assertEquals("Duplicate course", detail.getDetail());
    }

    @Test
    @DisplayName("Should return 409 Problem Detail for DataIntegrityViolationException")
    void testHandleDataIntegrityViolationException() {
        ProblemDetail detail = advice
                .handleDataIntegrityViolationException(new DataIntegrityViolationException("duplicate key"));
        assertEquals(409, detail.getStatus());
        assertEquals("The operation conflicts with existing data", detail.getDetail());
    }

    @Test
    @DisplayName("Should return 400 Problem Detail with field errors for ConstraintViolationException")
    void testHandleConstraintViolationException() {
        Course course = new Course();
        Set<ConstraintViolation<Course>> violations = Validation.buildDefaultValidatorFactory()
                .getValidator().validate(course);
        assertTrue(!violations.isEmpty());

        ProblemDetail detail = advice
                .handleConstraintViolationException(new ConstraintViolationException(violations));
        assertEquals(400, detail.getStatus());
        assertEquals("Validation failed", detail.getDetail());
        @SuppressWarnings("unchecked")
        var errors = (java.util.List<ApplicationControllerAdvice.FieldValidationError>) detail.getProperties()
                .get("errors");
        assertEquals(violations.size(), errors.size());
        assertTrue(errors.stream().allMatch(error -> error != null && error.field() != null));
    }
}
