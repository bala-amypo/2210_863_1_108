package com.example.demo.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, Map<String, Object>> userStore = new HashMap<>();
    private Long userIdCounter = 1L;

    public Map<String, Object> registerUser(String name, String email, String encodedPassword, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userIdCounter++);
        user.put("name", name);
        user.put("email", email);
        user.put("password", encodedPassword);
        user.put("role", role);
        
        userStore.put(email, user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Map<String, Object> user = userStore.get(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        return User.builder()
                .username(email)
                .password((String) user.get("password"))
                .roles((String) user.get("role"))
                .build();
    }

    public Map<String, Object> getUserByEmail(String email) {
        return userStore.get(email);
    }
}