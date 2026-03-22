package com.enginai.backend.course.service;

import com.enginai.backend.authn.entity.User;
import com.enginai.backend.authn.repository.UserRepository;
import com.enginai.backend.course.dto.*;
import com.enginai.backend.course.entity.*;
import com.enginai.backend.course.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePageRepository coursePageRepository;
    private final UserRepository userRepository;
    private final UserCourseEnrollmentRepository enrollmentRepository;
    private final UserPageCompletionRepository pageCompletionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final UserQuizSubmissionRepository quizSubmissionRepository;

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

    @Transactional
    public void completeTutorialPage(Long courseId, Long pageId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        CoursePage page = coursePageRepository.findById(pageId).orElseThrow();

        // Idempotent — skip if already completed
        if (!pageCompletionRepository.existsByUser_IdAndPage_Id(userId, pageId)) {
            UserPageCompletion completion = new UserPageCompletion();
            completion.setUser(user);
            completion.setPage(page);
            pageCompletionRepository.save(completion);
        }

        // Advance currentPage in enrollment to next page
        enrollmentRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(enrollment -> {
            List<CoursePage> allPages = courseRepository.findByIdWithPages(courseId).orElseThrow().getPages();
            for (int i = 0; i < allPages.size() - 1; i++) {
                if (allPages.get(i).getId().equals(pageId)) {
                    enrollment.setCurrentPage(allPages.get(i + 1));
                    enrollmentRepository.save(enrollment);
                    break;
                }
            }
        });
    }

    @Transactional
    public List<QuestionResultDto> submitQuiz(Long courseId, Long pageId, Long userId, QuizSubmitRequest request) {
        // Idempotent — return existing results if already submitted
        if (quizSubmissionRepository.findByUserIdAndPageId(userId, pageId).isPresent()) {
            throw new RuntimeException("Quiz already submitted");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        CoursePage page = coursePageRepository.findById(pageId).orElseThrow();

        List<QuizQuestion> questions = quizQuestionRepository.findByPageIdWithDetails(pageId);

        // Build submission
        UserQuizSubmission submission = new UserQuizSubmission();
        submission.setUser(user);
        submission.setCourse(course);
        submission.setPage(page);

        List<QuestionResultDto> results = new ArrayList<>();

        for (QuizQuestion q : questions) {
            String submitted = request.answers().get(q.getId());

            UserQuizAnswer answer = new UserQuizAnswer();
            answer.setSubmission(submission);
            answer.setQuestion(q);
            answer.setAnswerText(submitted != null ? submitted : "");

            QuestionResultDto result;

            if (q.getType() == QuizQuestion.QuestionType.MCQ) {
                String correctOption = q.getOptions().stream()
                        .filter(McqOption::getIsCorrect)
                        .map(McqOption::getOptionText)
                        .findFirst().orElse(null);
                boolean correct = correctOption != null && correctOption.equals(submitted);
                answer.setIsCorrect(correct);
                result = new QuestionResultDto(q.getId(), "mcq", correct, correctOption, null, submitted);
            } else {
                String modelAnswer = q.getModelAnswer() != null ? q.getModelAnswer().getModelAnswer() : null;
                answer.setIsCorrect(null);
                result = new QuestionResultDto(q.getId(), "written", null, null, modelAnswer, submitted);
            }

            submission.getAnswers().add(answer);
            results.add(result);
        }

        quizSubmissionRepository.save(submission);

        // Mark page as completed
        UserPageCompletion completion = new UserPageCompletion();
        completion.setUser(user);
        completion.setPage(page);
        pageCompletionRepository.save(completion);

        // Advance currentPage in enrollment to next page
        enrollmentRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(enrollment -> {
            List<CoursePage> allPages = courseRepository.findByIdWithPages(courseId).orElseThrow().getPages();
            for (int i = 0; i < allPages.size() - 1; i++) {
                if (allPages.get(i).getId().equals(pageId)) {
                    enrollment.setCurrentPage(allPages.get(i + 1));
                    enrollmentRepository.save(enrollment);
                    break;
                }
            }
        });

        return results;
    }

    @Transactional
    public List<QuestionResultDto> getQuizResult(Long courseId, Long pageId, Long userId) {
        UserQuizSubmission submission = quizSubmissionRepository
                .findByUserIdAndPageIdWithAnswers(userId, pageId)
                .orElseThrow(() -> new RuntimeException("No submission found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByPageIdWithDetails(pageId);
        Map<Long, QuizQuestion> questionMap = questions.stream()
                .collect(java.util.stream.Collectors.toMap(QuizQuestion::getId, q -> q));

        return submission.getAnswers().stream().map(answer -> {
            QuizQuestion q = questionMap.get(answer.getQuestion().getId());
            if (q.getType() == QuizQuestion.QuestionType.MCQ) {
                String correctOption = q.getOptions().stream()
                        .filter(McqOption::getIsCorrect)
                        .map(McqOption::getOptionText)
                        .findFirst().orElse(null);
                return new QuestionResultDto(q.getId(), "mcq", answer.getIsCorrect(), correctOption, null, answer.getAnswerText());
            } else {
                String modelAnswer = q.getModelAnswer() != null ? q.getModelAnswer().getModelAnswer() : null;
                return new QuestionResultDto(q.getId(), "written", null, null, modelAnswer, answer.getAnswerText());
            }
        }).toList();
    }
}
