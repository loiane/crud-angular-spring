package com.loiane.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LessonTest {

    private Lesson createLesson(Long id) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setName("Introduction");
        lesson.setYoutubeUrl("dQw4w9WgXcQ");
        return lesson;
    }

    @Test
    @DisplayName("Should expose fields through getters and setters")
    void testGettersAndSetters() {
        Lesson lesson = createLesson(1L);
        Course course = new Course();
        lesson.setCourse(course);

        assertEquals(1L, lesson.getId());
        assertEquals("Introduction", lesson.getName());
        assertEquals("dQw4w9WgXcQ", lesson.getYoutubeUrl());
        assertEquals(course, lesson.getCourse());
    }

    @Test
    @DisplayName("Should base equality on the id only")
    void testEquals() {
        Lesson lesson = createLesson(1L);
        Lesson sameId = createLesson(1L);
        sameId.setName("Different name");

        assertEquals(lesson, lesson);
        assertEquals(lesson, sameId);
        assertNotEquals(lesson, createLesson(2L));
        assertNotEquals(lesson, null);
        assertNotEquals(lesson, new Object());

        // Unsaved lessons (id == null) are only equal to themselves
        Lesson unsaved = createLesson(null);
        assertEquals(unsaved, unsaved);
        assertNotEquals(unsaved, createLesson(null));
    }

    @Test
    @DisplayName("Should have a constant hashCode per class")
    void testHashCode() {
        assertEquals(Lesson.class.hashCode(), createLesson(1L).hashCode());
        assertEquals(createLesson(1L).hashCode(), createLesson(2L).hashCode());
    }

    @Test
    @DisplayName("Should include id, name and youtubeUrl in toString")
    void testToString() {
        String text = createLesson(1L).toString();
        assertTrue(text.contains("id=1"));
        assertTrue(text.contains("name=Introduction"));
        assertTrue(text.contains("youtubeUrl=dQw4w9WgXcQ"));
    }
}
