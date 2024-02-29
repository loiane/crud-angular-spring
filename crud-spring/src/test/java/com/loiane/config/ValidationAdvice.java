package com.loiane.config;

import java.lang.reflect.Method;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.executable.ExecutableValidator;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.lang.Nullable;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ValidationAdvice implements MethodBeforeAdvice {

    static private final ExecutableValidator executableValidator;

    static {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet();
        executableValidator = factory.getValidator().forExecutables();
        factory.close();
    }

    @SuppressWarnings("null")
    @Override
    public void before(Method method, Object[] args, @Nullable Object target) {
        Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(target, method, args);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
