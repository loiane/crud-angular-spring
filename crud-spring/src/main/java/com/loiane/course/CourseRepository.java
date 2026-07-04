package com.loiane.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds courses by exact name. Used to enforce unique course names.
     */
    List<Course> findByName(String name);

    /**
     * Case-insensitive partial-match search, used by the search endpoint.
     */
    List<Course> findByNameContainingIgnoreCase(String name);
}
