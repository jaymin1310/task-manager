package com.jaymin.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false , length=100)
    private String name;
    @Column(nullable = false , length=100)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;
    @Column(name="updated_at",nullable = false)
    private LocalDateTime updatedAt;
    @Column(name="token_version",nullable = false)
    private Integer tokenVersion=0;
    @Column(name="is_verified",nullable = false)
    private Boolean isVerified;
    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    private List<Task> tasks;
    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshTokens;
}
