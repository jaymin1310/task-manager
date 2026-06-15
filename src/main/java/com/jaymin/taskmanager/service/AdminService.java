package com.jaymin.taskmanager.service;

import com.jaymin.taskmanager.entity.Role;
import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.OtpRepository;
import com.jaymin.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        otpRepository.deleteByUser(user);
        userRepository.delete(user);
    }
    public void makeAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }


}
