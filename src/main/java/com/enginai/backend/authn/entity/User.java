package com.enginai.backend.authn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String picture;

    private String contactNo;

    // Google OAuth subject ID
    @Column(nullable = false, unique = true)
    private String googleId;

    public User(String googleId, String email, String name, String picture) {
        this.googleId = googleId;
        this.email    = email;
        this.name     = name;
        this.picture  = picture;
    }
}
