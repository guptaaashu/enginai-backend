package com.enginai.backend.course.entity;

import com.enginai.backend.authn.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_course_enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter @Setter @NoArgsConstructor
public class UserCourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_page_id")
    private CoursePage currentPage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();
}
