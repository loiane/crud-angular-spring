package com.loiane.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CourseTest {

    private Lesson createLesson() {
        Lesson lesson = new Lesson();
        lesson.setName("Introduction");
        lesson.setYoutubeUrl("dQw4w9WgXcQ");
        return lesson;
    }

    @Test
    @DisplayName("Should reject null lessons collection")
    void testSetLessonsNull() {
        Course course = new Course();
        assertThrows(IllegalArgumentException.class, () -> course.setLessons(null));
    }

    @Test
    @DisplayName("Should reject adding or removing a null lesson")
    void testAddRemoveNullLesson() {
        Course course = new Course();
        assertThrows(IllegalArgumentException.class, () -> course.addLesson(null));
        assertThrows(IllegalArgumentException.class, () -> course.removeLesson(null));
    }

    @Test
    @DisplayName("Should wire the lesson back-reference when adding and removing")
    void testAddRemoveLesson() {
        Course course = new Course();
        Lesson lesson = createLesson();

        course.setLessons(Set.of(lesson));
        assertEquals(course, lesson.getCourse());
        assertTrue(course.getLessons().contains(lesson));

        course.removeLesson(lesson);
        assertNull(lesson.getCourse());
        assertTrue(course.getLessons().isEmpty());
    }

    @Test
    @DisplayName("Should replace existing lessons when setting a new collection")
    void testSetLessonsReplaces() {
        Course course = new Course();
        Lesson first = createLesson();
        course.setLessons(Set.of(first));

        Lesson second = createLesson();
        second.setName("Second lesson");
        course.setLessons(Set.of(second));

        assertEquals(Set.of(second), course.getLessons());
        assertFalse(course.getLessons().contains(first));
    }
}
