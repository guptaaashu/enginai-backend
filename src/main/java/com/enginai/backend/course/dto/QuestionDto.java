package com.enginai.backend.course.dto;

import com.enginai.backend.course.entity.McqOption;
import com.enginai.backend.course.entity.QuizQuestion;

import java.util.List;

public record QuestionDto(
        Long id,
        String type,
        String question,
        List<String> options  // null for written questions
) {
    public static QuestionDto from(QuizQuestion q) {
        String type = q.getType() == QuizQuestion.QuestionType.MCQ ? "mcq" : "written";
        List<String> options = q.getType() == QuizQuestion.QuestionType.MCQ
                ? q.getOptions().stream().map(McqOption::getOptionText).toList()
                : null;
        return new QuestionDto(q.getId(), type, q.getQuestionText(), options);
    }
}
