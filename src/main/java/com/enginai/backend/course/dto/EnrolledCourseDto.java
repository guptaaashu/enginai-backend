package com.enginai.backend.course.dto;

import com.enginai.backend.course.entity.UserCourseEnrollment;

public record EnrolledCourseDto(
        Long id,
        String title,
        String thumbnailUrl,
        String level,
        String category,
        long completedPages,
        long totalPages,
        Long currentPageId
) {
    public static EnrolledCourseDto from(UserCourseEnrollment enrollment, long completedPages, long totalPages) {
        var course = enrollment.getCourse();
        return new EnrolledCourseDto(
                course.getId(),
                course.getTitle(),
                course.getThumbnailUrl(),
                course.getLevel().name(),
                course.getCategory(),
                completedPages,
                totalPages,
                enrollment.getCurrentPage() != null ? enrollment.getCurrentPage().getId() : null
        );
    }
}
