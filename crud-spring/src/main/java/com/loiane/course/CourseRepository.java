package com.loiane.course;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds courses by exact name, bypassing the SQLRestriction so soft-deleted
     * courses are included. Used to enforce unique course names, mirroring the
     * unique constraint on the name column, which also covers soft-deleted rows.
     */
    @Query(value = "SELECT * FROM course WHERE name = :name", nativeQuery = true)
    List<Course> findByNameIgnoringRestriction(@Param("name") String name);

    /**
     * Case-insensitive partial-match search, used by the search endpoint. The
     * result is capped so a broad query cannot return the whole table.
     */
    List<Course> findByNameContainingIgnoreCase(String name, Limit limit);
}
