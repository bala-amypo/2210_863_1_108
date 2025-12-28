package com.example.demo.controller;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    
    @Operation(
        summary = "Register a new user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = @ExampleObject(
                    value = "{\"name\":\"Admin User\",\"email\":\"admin@example.com\",\"password\":\"admin123\",\"role\":\"ADMIN\"}"
                )
            )
        )
    )
  
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.getOrDefault("role", "USER");

        String encodedPassword = passwordEncoder.encode(password);
        Map<String, Object> user = userDetailsService.registerUser(name, email, encodedPassword, role);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", user.get("userId"));
        response.put("email", email);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    
    @Operation(
        summary = "Login and get JWT token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = @ExampleObject(
                    value = "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"
                )
            )
        )
    )
    
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Map<String, Object> user = userDetailsService.getUserByEmail(email);
        String token = jwtTokenProvider.generateToken(
                authentication,
                (Long) user.get("userId"),
                (String) user.get("role")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("userId", user.get("userId"));
        response.put("email", email);
        response.put("role", user.get("role"));

        return ResponseEntity.ok(response);
    }
}