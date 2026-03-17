package com.enginai.backend.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mcq_option")
@Getter @Setter @NoArgsConstructor
public class McqOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(nullable = false)
    private Boolean isCorrect;
}
