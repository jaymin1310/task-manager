package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.dto.request.*;
import com.jaymin.taskmanager.dto.response.ApiResponse;
import com.jaymin.taskmanager.dto.response.AuthResponse;
import com.jaymin.taskmanager.dto.response.OtpResponse;
import com.jaymin.taskmanager.entity.OtpType;
import com.jaymin.taskmanager.entity.RefreshToken;
import com.jaymin.taskmanager.entity.Role;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.UserRepository;
import com.jaymin.taskmanager.security.CustomUserDetailsService;
import com.jaymin.taskmanager.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final OtpService otpService;
    private final EmailService emailService;

    public ApiResponse register(RegisterRequest request) {
        Optional<User> existingUser =
                userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsVerified()) {
                throw new RuntimeException("Email is already registered");
            }
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

            OtpResponse otpResponse= otpService.generateAndSendOtp(user,OtpType.EMAIL_VERIFICATION);

            return ApiResponse.builder()
                    .message(otpResponse.getMessage())
                    .success(otpResponse.isSuccess())
                    .build();
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .tokenVersion(0)
                .isVerified(false)
                .build();
        userRepository.save(user);
        OtpResponse otpResponse= otpService.generateAndSendOtp(user,OtpType.EMAIL_VERIFICATION);
        return ApiResponse.builder()
                .message(otpResponse.getMessage())
                .success(otpResponse.isSuccess())
                .build();
    }
    public ApiResponse resetPassword(ResetPasswordRequest request){
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ApiResponse.builder()
                    .message("Invalid email or OTP")
                    .success(false)
                    .build();
        }
        User user = userOpt.get();
        otpService.validateOtp(user,request.getOtp(),OtpType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion()+1);
        userRepository.save(user);
        refreshTokenService.deleteByUser(user);
        return ApiResponse.builder()
                .message("Password reset successfully")
                .success(true)
                .build();
    }
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!user.getIsVerified()) {
            throw new RuntimeException("User not verified");
        }
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken =
                jwtService.generateAccessToken(userDetails, user.getTokenVersion());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user, userDetails, user.getTokenVersion());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String newAccessToken =
                jwtService.generateAccessToken(userDetails, user.getTokenVersion());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
    public void logout(String accessToken) {

        String username = jwtService.extractUsername(accessToken);
        Integer tokenVersion = jwtService.extractTokenVersion(accessToken);
        User user = userRepository.findByEmail(username).orElseThrow();
        if (!tokenVersion.equals(user.getTokenVersion())) {
            throw new RuntimeException("Invalid or expired token");
        }
        // Increment version (INVALIDATES ALL ACCESS TOKENS)
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
        refreshTokenService.deleteByUser(user);
    }
    public ApiResponse resendVerificationOtp(OtpRequest request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || userOpt.get().getIsVerified()) {
            return ApiResponse.builder()
                    .message("If account exists, OTP has been sent")
                    .success(true)
                    .build();
        }

        User user = userOpt.get();
        OtpResponse otpResponse=otpService.generateAndSendOtp(user,OtpType.EMAIL_VERIFICATION);
        return ApiResponse.builder()
                .message(otpResponse.getMessage())
                .success(otpResponse.isSuccess())
                .build();
    }
    public ApiResponse resendResetOtp(OtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ApiResponse.builder()
                    .message("If account exists, OTP has been sent")
                    .success(true)
                    .build();
        }
        User user = userOpt.get();
        OtpResponse otpResponse=otpService.generateAndSendOtp(user,OtpType.PASSWORD_RESET);
        return ApiResponse.builder()
                .message(otpResponse.getMessage())
                .success(otpResponse.isSuccess())
                .build();
    }
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or OTP");
        }
        User user = userOpt.get();
        otpService.validateOtp(user,request.getOtp(),OtpType.EMAIL_VERIFICATION);
        user.setIsVerified(true);
        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken =
                jwtService.generateAccessToken(userDetails, user.getTokenVersion());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user, userDetails, user.getTokenVersion());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
    public ApiResponse forgotPassword(OtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ApiResponse.builder()
                    .message("If account exists, OTP has been sent")
                    .success(true)
                    .build();
        }
        User user = userOpt.get();
        OtpResponse otpResponse=otpService.generateAndSendOtp(user,OtpType.PASSWORD_RESET);
        return ApiResponse.builder()
                .message(otpResponse.getMessage())
                .success(otpResponse.isSuccess())
                .build();
    }
}
