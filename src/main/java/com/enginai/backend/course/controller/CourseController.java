package com.enginai.backend.course.controller;

import com.enginai.backend.course.dto.*;
import com.enginai.backend.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/enrolled")
    public ResponseEntity<List<EnrolledCourseDto>> getEnrolledCourses() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(courseService.getEnrolledCourses(userId));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailDto> getCourseDetail(@PathVariable Long courseId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(courseService.getCourseDetail(courseId, userId));
    }

    @GetMapping("/{courseId}/pages/{pageId}")
    public ResponseEntity<PageContentDto> getPageContent(@PathVariable Long courseId,
                                                          @PathVariable Long pageId) {
        return ResponseEntity.ok(courseService.getPageContent(courseId, pageId));
    }

    @PostMapping("/{courseId}/pages/{pageId}/complete")
    public ResponseEntity<Void> completeTutorialPage(@PathVariable Long courseId,
                                                      @PathVariable Long pageId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        courseService.completeTutorialPage(courseId, pageId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/quiz/{pageId}/submit")
    public ResponseEntity<List<QuestionResultDto>> submitQuiz(@PathVariable Long courseId,
                                                               @PathVariable Long pageId,
                                                               @RequestBody QuizSubmitRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(courseService.submitQuiz(courseId, pageId, userId, request));
    }

    @GetMapping("/{courseId}/quiz/{pageId}/result")
    public ResponseEntity<List<QuestionResultDto>> getQuizResult(@PathVariable Long courseId,
                                                                   @PathVariable Long pageId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(courseService.getQuizResult(courseId, pageId, userId));
    }
}
