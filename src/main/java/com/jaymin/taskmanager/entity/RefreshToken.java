package com.jaymin.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    private LocalDateTime expiredDate;
    private LocalDateTime createdAt;
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;

}
