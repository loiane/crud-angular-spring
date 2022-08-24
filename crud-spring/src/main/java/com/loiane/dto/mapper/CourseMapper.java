package com.loiane.dto.mapper;

import org.springframework.stereotype.Component;

import com.loiane.dto.CourseRequestDTO;
import com.loiane.model.Course;

/**
 * Class to map the Course entity to the CourseRequestDTO and vice-versa.
 * ModelMapper currently does not support record types.
 */
@Component
public class CourseMapper {

    public Course dtoToModel(CourseRequestDTO courseRequestDTO) {
        return Course.builder().name(courseRequestDTO.name()).category(courseRequestDTO.category()).build();
    }
}
