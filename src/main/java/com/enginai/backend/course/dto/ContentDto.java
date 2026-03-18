package com.enginai.backend.course.dto;

import java.util.List;

public record ContentDto(
        String heading,            // tutorial only
        List<SectionDto> sections, // tutorial only
        String takeaway,           // tutorial only
        List<QuestionDto> questions // quiz only
) {}
