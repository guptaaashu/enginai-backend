package com.enginai.backend.course.dto;

import com.enginai.backend.course.entity.CoursePage;

public record PageStubDto(
        Long id,
        String title,
        String type,
        Integer pageOrder
) {
    public static PageStubDto from(CoursePage page) {
        String type = page.getType() == CoursePage.PageType.TUTORIAL ? "tutorial" : "question";
        return new PageStubDto(page.getId(), page.getTitle(), type, page.getPageOrder());
    }
}
