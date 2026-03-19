package com.jaymin.taskmanager.repository;

import com.jaymin.taskmanager.entity.RefreshToken;
import com.jaymin.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByExpiredDateBefore(LocalDateTime now);
    void deleteByUser(User user);
}
