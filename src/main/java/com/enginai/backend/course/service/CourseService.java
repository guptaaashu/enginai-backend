package com.enginai.backend.course.service;

import com.enginai.backend.course.dto.CourseDto;
import com.enginai.backend.course.dto.EnrolledCourseDto;
import com.enginai.backend.course.repository.CoursePageRepository;
import com.enginai.backend.course.repository.CourseRepository;
import com.enginai.backend.course.repository.UserCourseEnrollmentRepository;
import com.enginai.backend.course.repository.UserPageCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserCourseEnrollmentRepository enrollmentRepository;
    private final CoursePageRepository coursePageRepository;
    private final UserPageCompletionRepository pageCompletionRepository;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseDto::from)
                .toList();
    }

    public List<EnrolledCourseDto> getEnrolledCourses(Long userId) {
        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(enrollment -> {
                    Long courseId = enrollment.getCourse().getId();
                    long totalPages = coursePageRepository.countByCourseId(courseId);
                    long completedPages = pageCompletionRepository.countByUserIdAndCourseId(userId, courseId);
                    return EnrolledCourseDto.from(enrollment, completedPages, totalPages);
                })
                .toList();
    }
}
