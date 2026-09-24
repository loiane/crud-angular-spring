package com.loiane.shared.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.loiane.course.Course;
import com.loiane.course.CourseController;
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

    @Test
    @DisplayName("Should return 400 Problem Detail for MethodArgumentTypeMismatchException")
    void testHandleMethodArgumentTypeMismatchException() throws NoSuchMethodException {
        MethodParameter param = new MethodParameter(
                CourseController.class.getMethod("findById", Long.class), 0);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "id", param, new NumberFormatException("For input string: \"abc\""));

        ProblemDetail detail = advice.handleMethodArgumentTypeMismatchException(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Validation failed", detail.getDetail());
        @SuppressWarnings("unchecked")
        var errors = (java.util.List<ApplicationControllerAdvice.FieldValidationError>) detail.getProperties()
                .get("errors");
        assertEquals(1, errors.size());
        assertEquals("id", errors.get(0).field());
        assertTrue(errors.get(0).message().contains("Long"));
    }

    @Test
    @DisplayName("Should return 400 Problem Detail for MissingServletRequestParameterException")
    void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "String");

        ProblemDetail detail = advice.handleMissingServletRequestParameterException(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Validation failed", detail.getDetail());
        @SuppressWarnings("unchecked")
        var errors = (java.util.List<ApplicationControllerAdvice.FieldValidationError>) detail.getProperties()
                .get("errors");
        assertEquals(1, errors.size());
        assertEquals("name", errors.get(0).field());
    }
}
