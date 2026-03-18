package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.UserPageCompletion;
import com.enginai.backend.course.entity.UserPageCompletionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPageCompletionRepository extends JpaRepository<UserPageCompletion, UserPageCompletionId> {

    @Query("SELECT COUNT(c) FROM UserPageCompletion c WHERE c.user.id = :userId AND c.page.course.id = :courseId")
    long countByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT c.page.id FROM UserPageCompletion c WHERE c.user.id = :userId AND c.page.course.id = :courseId")
    List<Long> findCompletedPageIds(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
