package com.loiane.config;

import java.lang.reflect.Method;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.executable.ExecutableValidator;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

public class ValidationAdvice implements MethodBeforeAdvice {

    private final ExecutableValidator executableValidator;
    private final LocalValidatorFactoryBean factory;

    public ValidationAdvice(ApplicationContext applicationContext) {
        factory = new LocalValidatorFactoryBean();
        factory.setApplicationContext(applicationContext);
        factory.setConstraintValidatorFactory(
                new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory()));
        factory.afterPropertiesSet();
        executableValidator = factory.getValidator().forExecutables();
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
