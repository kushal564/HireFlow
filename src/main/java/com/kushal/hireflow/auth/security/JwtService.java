package com.kushal.hireflow.auth.security;

import com.kushal.hireflow.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.access-secret}")
    private String accessSecret;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;


    private SecretKey getAccessSecretKey() {

        return Keys.hmacShaKeyFor(
                accessSecret.getBytes(StandardCharsets.UTF_8)
        );
    }


    private SecretKey getRefreshSecretKey() {

        return Keys.hmacShaKeyFor(
                refreshSecret.getBytes(StandardCharsets.UTF_8)
        );
    }


    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim(
                        "role",
                        user.getRole().getName().name()
                )
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessExpiration
                        )
                )
                .signWith(getAccessSecretKey())
                .compact();
    }


    public String generateRefreshToken(User user) {

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )
                .signWith(getRefreshSecretKey())
                .compact();
    }


    public Long getUserIdFromAccessToken(String token) {

        return Long.valueOf(
                getAccessClaims(token)
                        .getSubject()
        );
    }


    public Long getUserIdFromRefreshToken(String token) {

        return Long.valueOf(
                getRefreshClaims(token)
                        .getSubject()
        );
    }


    public String getJtiFromRefreshToken(String token) {

        return getRefreshClaims(token)
                .getId();
    }


    public boolean isAccessTokenValid(
            String token,
            Long userId) {

        Claims claims =
                getAccessClaims(token);

        return claims.getSubject()
                .equals(userId.toString())
                && claims.getExpiration()
                .after(new Date());
    }


    public Date getRefreshExpiration(String token) {

        return getRefreshClaims(token)
                .getExpiration();
    }


    public boolean isRefreshTokenValid(
            String token,
            Long userId) {

        Claims claims =
                getRefreshClaims(token);

        return claims.getSubject()
                .equals(userId.toString())
                && claims.getExpiration()
                .after(new Date());
    }


    private Claims getAccessClaims(String token) {

        return Jwts.parser()
                .verifyWith(getAccessSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private Claims getRefreshClaims(String token) {

        return Jwts.parser()
                .verifyWith(getRefreshSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}