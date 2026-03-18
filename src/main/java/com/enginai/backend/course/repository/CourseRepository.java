package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.pages WHERE c.id = :id")
    Optional<Course> findByIdWithPages(@Param("id") Long id);
}
