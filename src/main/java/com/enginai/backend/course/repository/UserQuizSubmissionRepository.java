package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.UserQuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserQuizSubmissionRepository extends JpaRepository<UserQuizSubmission, Long> {

    Optional<UserQuizSubmission> findByUserIdAndPageId(Long userId, Long pageId);

    @Query("SELECT s FROM UserQuizSubmission s JOIN FETCH s.answers WHERE s.user.id = :userId AND s.page.id = :pageId")
    Optional<UserQuizSubmission> findByUserIdAndPageIdWithAnswers(@Param("userId") Long userId, @Param("pageId") Long pageId);
}
