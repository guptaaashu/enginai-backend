package com.enginai.backend.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_page")
@Getter @Setter @NoArgsConstructor
public class CoursePage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer pageOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PageType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String takeaway;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sectionOrder ASC")
    private List<TutorialSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private List<QuizQuestion> questions = new ArrayList<>();

    public enum PageType {
        TUTORIAL, QUIZ
    }
}
