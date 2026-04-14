package com.jaymin.taskmanager.scheduler;

import com.jaymin.taskmanager.repository.OtpRepository;
import com.jaymin.taskmanager.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpRepository otpRepository;
    @Scheduled(cron="0 */10 * * * *")
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiredDateBefore(now);
        otpRepository.deleteByExpiryTimeBefore(now);
        otpRepository.deleteByUsedTrue();
        System.out.println("Cleanup executed");
    }git
}
