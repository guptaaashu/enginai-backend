package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.UserQuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuizSubmissionRepository extends JpaRepository<UserQuizSubmission, Long> {

    Optional<UserQuizSubmission> findByUserIdAndPageId(Long userId, Long pageId);
}
