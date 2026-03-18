package com.enginai.backend.course.service;

import com.enginai.backend.authn.entity.User;
import com.enginai.backend.authn.repository.UserRepository;
import com.enginai.backend.course.dto.*;
import com.enginai.backend.course.entity.*;
import com.enginai.backend.course.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePageRepository coursePageRepository;
    private final UserRepository userRepository;
    private final UserCourseEnrollmentRepository enrollmentRepository;
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

    @Transactional
    public CourseDetailDto getCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findByIdWithPages(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<CoursePage> pages = course.getPages();

        // Auto-enroll on first visit
        UserCourseEnrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    UserCourseEnrollment e = new UserCourseEnrollment();
                    e.setUser(user);
                    e.setCourse(course);
                    e.setCurrentPage(pages.isEmpty() ? null : pages.get(0));
                    return enrollmentRepository.save(e);
                });

        List<Long> completedPageIds = pageCompletionRepository.findCompletedPageIds(userId, courseId);

        Long currentPageId = enrollment.getCurrentPage() != null
                ? enrollment.getCurrentPage().getId()
                : (pages.isEmpty() ? null : pages.get(0).getId());

        List<PageStubDto> stubs = pages.stream().map(PageStubDto::from).toList();

        return new CourseDetailDto(
                course.getId(), course.getTitle(), course.getLevel().name(),
                course.getCategory(), stubs, completedPageIds, currentPageId
        );
    }

    @Transactional
    public PageContentDto getPageContent(Long courseId, Long pageId) {
        CoursePage page = coursePageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        List<CoursePage> allPages = courseRepository.findByIdWithPages(courseId)
                .orElseThrow().getPages();

        if (page.getType() == CoursePage.PageType.TUTORIAL) {
            int chapterNum = (int) allPages.stream()
                    .filter(p -> p.getType() == CoursePage.PageType.TUTORIAL
                            && p.getPageOrder() <= page.getPageOrder())
                    .count();

            List<SectionDto> sections = page.getSections().stream()
                    .map(SectionDto::from)
                    .toList();

            ContentDto content = new ContentDto(page.getTitle(), sections, page.getTakeaway(), null);
            return new PageContentDto(page.getId(), page.getTitle(), "tutorial", chapterNum, null, content);

        } else {
            int quizNum = (int) allPages.stream()
                    .filter(p -> p.getType() == CoursePage.PageType.QUIZ
                            && p.getPageOrder() <= page.getPageOrder())
                    .count();

            List<QuestionDto> questions = page.getQuestions().stream()
                    .map(QuestionDto::from)
                    .toList();

            ContentDto content = new ContentDto(null, null, null, questions);
            return new PageContentDto(page.getId(), page.getTitle(), "question", null, quizNum, content);
        }
    }
}
