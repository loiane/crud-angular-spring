package com.loiane.course;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Test-only repository with queries that bypass the entity mappings,
 * such as the @SQLRestriction that hides soft-deleted courses.
 */
public interface CourseTestRepository extends Repository<Course, Long> {

    /**
     * Find a course by ID bypassing the SQLRestriction to include soft-deleted
     * courses. Used to verify the soft delete behavior.
     */
    @Query(value = "SELECT * FROM course WHERE id = :id", nativeQuery = true)
    Optional<Course> findByIdIgnoringRestriction(@Param("id") Long id);
}
