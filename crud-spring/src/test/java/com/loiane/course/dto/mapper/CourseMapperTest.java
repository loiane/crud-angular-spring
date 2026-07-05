package com.loiane.course.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loiane.course.Course;
import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.dto.LessonDTO;
import com.loiane.course.enums.Category;

class CourseMapperTest {

    private final CourseMapper mapper = new CourseMapper();

    @Test
    @DisplayName("Should return null when mapping a null course to DTO")
    void testToDTONull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    @DisplayName("Should return null when converting a null category value")
    void testConvertCategoryValueNull() {
        assertNull(mapper.convertCategoryValue(null));
    }

    @Test
    @DisplayName("Should map a request DTO to a course and back")
    void testRoundTrip() {
        CourseRequestDTO request = new CourseRequestDTO("Spring Boot Fundamentals", "Back-end",
                List.of(new LessonDTO(0L, "Introduction", "dQw4w9WgXcQ")));

        Course course = mapper.toModel(request);
        assertEquals("Spring Boot Fundamentals", course.getName());
        assertEquals(Category.BACK_END, course.getCategory());
        assertEquals(1, course.getLessons().size());

        CourseDTO dto = mapper.toDTO(course);
        assertEquals("Spring Boot Fundamentals", dto.name());
        assertEquals("Back-end", dto.category());
        assertEquals(1, dto.lessons().size());
        LessonDTO lessonDTO = dto.lessons().get(0);
        assertEquals("Introduction", lessonDTO.name());
        assertEquals("dQw4w9WgXcQ", lessonDTO.youtubeUrl());
    }
}
