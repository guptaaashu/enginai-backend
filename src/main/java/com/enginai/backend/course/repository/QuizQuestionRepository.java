package com.enginai.backend.course.repository;

import com.enginai.backend.course.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    @Query("SELECT q FROM QuizQuestion q LEFT JOIN FETCH q.options LEFT JOIN FETCH q.modelAnswer WHERE q.page.id = :pageId ORDER BY q.questionOrder")
    List<QuizQuestion> findByPageIdWithDetails(@Param("pageId") Long pageId);
}
