package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.entity.RefreshToken;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.RefreshTokenRepository;
import com.jaymin.taskmanager.security.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public RefreshToken createRefreshToken(User user, UserDetails userDetails,int tokenVersion) {

        String token = jwtService.generateRefreshToken(userDetails,tokenVersion);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredDate(LocalDateTime.now()
                        .plusSeconds(refreshTokenExpiration / 1000))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiredDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(token);
            throw new RuntimeException("Refresh token expired");
        }

        try {
            jwtService.extractUsername(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token");
        }
        Integer tokenVersionFromJwt = jwtService.extractTokenVersion(token);
        User user = refreshToken.getUser();

        if (!tokenVersionFromJwt.equals(user.getTokenVersion())) {
            throw new RuntimeException("Refresh token is invalid due to version mismatch");
        }
        return refreshToken;
    }
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiredDateBefore(LocalDateTime.now());
    }
}
