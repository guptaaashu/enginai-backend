package com.enginai.backend.course.dto;

import com.enginai.backend.course.entity.Course;

public record CourseDto(
        Long id,
        String title,
        String description,
        String thumbnailUrl,
        String level,
        String category
) {
    public static CourseDto from(Course course) {
        return new CourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailUrl(),
                course.getLevel().name(),
                course.getCategory()
        );
    }
}
