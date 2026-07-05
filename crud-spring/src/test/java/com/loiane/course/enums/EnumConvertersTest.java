package com.loiane.course.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loiane.course.enums.converters.CategoryConverter;
import com.loiane.course.enums.converters.StatusConverter;

class EnumConvertersTest {

    private final StatusConverter statusConverter = new StatusConverter();
    private final CategoryConverter categoryConverter = new CategoryConverter();

    @Test
    @DisplayName("Should convert Status to and from its database value")
    void testStatusConverter() {
        assertEquals("Active", statusConverter.convertToDatabaseColumn(Status.ACTIVE));
        assertEquals(Status.INACTIVE, statusConverter.convertToEntityAttribute("Inactive"));
        assertNull(statusConverter.convertToDatabaseColumn(null));
        assertNull(statusConverter.convertToEntityAttribute(null));
    }

    @Test
    @DisplayName("Should convert Category to and from its database value")
    void testCategoryConverter() {
        assertEquals("Front-end", categoryConverter.convertToDatabaseColumn(Category.FRONT_END));
        assertEquals(Category.BACK_END, categoryConverter.convertToEntityAttribute("Back-end"));
        assertNull(categoryConverter.convertToDatabaseColumn(null));
        assertNull(categoryConverter.convertToEntityAttribute(null));
    }

    @Test
    @DisplayName("Should use the display value as toString")
    void testToString() {
        assertEquals("Active", Status.ACTIVE.toString());
        assertEquals("Front-end", Category.FRONT_END.toString());
    }

    @Test
    @DisplayName("Should reject unknown enum values")
    void testInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> Status.fromValue("Unknown"));
        assertThrows(IllegalArgumentException.class, () -> Category.fromValue("Unknown"));
    }
}
