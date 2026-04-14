package com.jaymin.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,length=6)
    private String code;
    @Column(nullable=false)
    private LocalDateTime createdAt;
    @Column(nullable=false)
    private LocalDateTime expiryTime;
    @Column(nullable=false)
    private boolean used;
    @Column(nullable=false)
    private OtpType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
