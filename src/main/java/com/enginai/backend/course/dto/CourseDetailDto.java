package com.enginai.backend.course.dto;

import java.util.List;

public record CourseDetailDto(
        Long id,
        String title,
        String level,
        String category,
        List<PageStubDto> pages,
        List<Long> completedPageIds,
        Long currentPageId
) {}
