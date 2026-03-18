package com.enginai.backend.course.dto;

import com.enginai.backend.course.entity.TutorialSection;

public record SectionDto(
        String type,
        String heading,
        String body
) {
    public static SectionDto from(TutorialSection section) {
        String type = section.getType() == TutorialSection.SectionType.TEXT ? "text" : "code";
        return new SectionDto(type, section.getHeading(), section.getContent());
    }
}
