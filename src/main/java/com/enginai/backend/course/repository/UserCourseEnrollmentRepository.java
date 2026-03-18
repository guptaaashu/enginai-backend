package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.UserCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCourseEnrollmentRepository extends JpaRepository<UserCourseEnrollment, Long> {

    @Query("SELECT e FROM UserCourseEnrollment e JOIN FETCH e.course WHERE e.user.id = :userId")
    List<UserCourseEnrollment> findByUserId(@Param("userId") Long userId);

    Optional<UserCourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
