package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.CoursePage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePageRepository extends JpaRepository<CoursePage, Long> {

    long countByCourseId(Long courseId);
}
