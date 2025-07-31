package com.loiane.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loiane.course.enums.Status;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByStatus(Pageable pageable, Status status);

    List<Course> findByName(String name);

    /**
     * Find a course by ID bypassing the SQLRestriction to include soft-deleted
     * courses.
     * This is useful for testing soft delete functionality.
     */
    @Query(value = "SELECT * FROM course WHERE id = :id", nativeQuery = true)
    Optional<Course> findByIdIgnoringRestriction(@Param("id") Long id);
}
