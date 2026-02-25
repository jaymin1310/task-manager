package com.jaymin.taskmanager.security;

import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email"+email));
        return new CustomUserDetails(user);
    }
}
