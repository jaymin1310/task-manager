package com.jaymin.taskmanager.repository;

import com.jaymin.taskmanager.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    long deleteByToken(String token);
    void deleteByExpiredDateBefore(LocalDateTime now);
}
