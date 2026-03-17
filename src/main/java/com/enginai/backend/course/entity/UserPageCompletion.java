package com.enginai.backend.course.entity;

import com.enginai.backend.authn.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_page_completion")
@IdClass(UserPageCompletionId.class)
@Getter @Setter @NoArgsConstructor
public class UserPageCompletion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private CoursePage page;

    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt = LocalDateTime.now();
}
