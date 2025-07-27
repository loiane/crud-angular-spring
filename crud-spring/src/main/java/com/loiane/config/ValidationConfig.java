package com.loiane.config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

/**
 * Configuration for Spring validation that enables dependency injection in
 * custom validators.
 */
@Configuration
public class ValidationConfig {

    /**
     * Creates a LocalValidatorFactoryBean that integrates Hibernate Validator with
     * Spring.
     * This allows custom constraint validators to have Spring beans injected into
     * them.
     * 
     * @param beanFactory the AutowireCapableBeanFactory for dependency injection
     * @return configured LocalValidatorFactoryBean
     */
    @Bean
    @Primary
    public LocalValidatorFactoryBean validator(AutowireCapableBeanFactory beanFactory) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setConstraintValidatorFactory(new SpringConstraintValidatorFactory(beanFactory));
        return factory;
    }

    /**
     * Creates a MethodValidationPostProcessor that uses the Spring-aware validator.
     * This enables method-level validation with Spring dependency injection.
     * 
     * @param beanFactory the AutowireCapableBeanFactory for dependency injection
     * @return configured MethodValidationPostProcessor
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(AutowireCapableBeanFactory beanFactory) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator(beanFactory));
        return processor;
    }
}
