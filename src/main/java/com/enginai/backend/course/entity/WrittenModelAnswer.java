package com.enginai.backend.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "written_model_answer")
@Getter @Setter @NoArgsConstructor
public class WrittenModelAnswer {

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "question_id")
    private QuizQuestion question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String modelAnswer;
}
