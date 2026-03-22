package com.enginai.backend.course.dto;

public record QuestionResultDto(
        Long questionId,
        String type,
        Boolean correct,        // true/false for MCQ, null for written
        String correctAnswer,   // correct option text for MCQ, null for written
        String modelAnswer      // model answer for written, null for MCQ
) {}
