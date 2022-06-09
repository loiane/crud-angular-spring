package com.loiane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CourseNotFoundException extends RuntimeException {
    
    public CourseNotFoundException( Long id) {
        super("Could not find course " + id);
    }
}
