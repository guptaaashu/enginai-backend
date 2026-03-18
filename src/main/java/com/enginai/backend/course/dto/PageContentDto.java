package com.enginai.backend.course.dto;

public record PageContentDto(
        Long id,
        String title,
        String type,
        Integer chapterNum, // tutorial only
        Integer quizNum,    // quiz only
        ContentDto content
) {}
