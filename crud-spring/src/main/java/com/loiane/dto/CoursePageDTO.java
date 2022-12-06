package com.loiane.dto;

import java.util.List;

/**
 * Used as response object that represents a Page with a list of Courses.
 */
public record CoursePageDTO(List<CourseDTO> courses, long totalElements, int totalPages) {

}
