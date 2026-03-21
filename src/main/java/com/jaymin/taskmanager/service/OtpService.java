package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.entity.Otp;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.OtpRepository;
import com.jaymin.taskmanager.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
    public void generateAndSendOtp(User user) {
        otpRepository.invalidateAllOtp(user);
        String code = generateOtp();

        Otp otp = Otp.builder()
                .code(code)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(otp);
        emailService.sendOtpEmail(user.getEmail(), code);
    }
    public void validateOtp(User user, String code) {

        Otp otp = otpRepository.findByCodeAndUser(code, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otp.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        otp.setUsed(true);
        otpRepository.save(otp);
    }
}
