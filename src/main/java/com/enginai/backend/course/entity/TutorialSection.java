package com.enginai.backend.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutorial_section")
@Getter @Setter @NoArgsConstructor
public class TutorialSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private CoursePage page;

    @Column(nullable = false)
    private Integer sectionOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionType type;

    private String heading;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String language;

    public enum SectionType {
        TEXT, CODE
    }
}
