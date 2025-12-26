package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:VerySecretKeyForJwtDemoApplication1234567890VerySecretKeyForJwtDemoApplication1234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    public JwtTokenProvider() {
        // Default constructor for Spring
    }

    // Constructor for testing - ensures key is at least 64 characters
    public JwtTokenProvider(String jwtSecret, Long jwtExpirationMs, Boolean enableJwt) {
        // Pad the key if it's too short
        if (jwtSecret != null && jwtSecret.length() < 64) {
            this.jwtSecret = jwtSecret + "0".repeat(64 - jwtSecret.length());
        } else {
            this.jwtSecret = jwtSecret;
        }
        this.jwtExpirationMs = jwtExpirationMs != null ? jwtExpirationMs : 3600000L;
    }

    public String generateToken(Authentication authentication, Long userId, String role) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("email", username);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public Map<String, Object> getAllClaims(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userId", claims.get("userId"));
        claimsMap.put("role", claims.get("role"));
        claimsMap.put("email", claims.get("email"));
        return claimsMap;
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}