package com.kushal.hireflow.auth.service;

import com.kushal.hireflow.auth.dto.AuthResponse;
import com.kushal.hireflow.auth.dto.LoginRequest;
import com.kushal.hireflow.auth.dto.RegisterRequest;
import com.kushal.hireflow.auth.dto.ResendVerificationEmailRequest;
import com.kushal.hireflow.auth.entity.RefreshToken;
import com.kushal.hireflow.auth.repository.RefreshTokenRepository;
import com.kushal.hireflow.auth.security.JwtService;
import com.kushal.hireflow.common.exception.BadRequestException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.user.repository.RoleRepository;
import com.kushal.hireflow.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int SESSION_LIMIT = 2;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public ResponseEntity<String> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(
                roleRepository.findByName(RoleName.CANDIDATE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Default role not found"
                                )
                        )
        );

        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        emailVerificationService
                .createVerificationTokenAndSendEmail(savedUser);

        return ResponseEntity.ok("Registration successful");
    }


    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Invalid email or password"
            );
        }

        if (!user.isEmailVerified()) {
            throw new BadRequestException(
                    "Email is not verified"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        createRefreshSession(user, refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }


    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException(
                    "Refresh token not found"
            );
        }

        try {

            Long userId =
                    jwtService.getUserIdFromRefreshToken(
                            refreshToken
                    );

            String jti =
                    jwtService.getJtiFromRefreshToken(
                            refreshToken
                    );

            if (jti == null || jti.isBlank()) {
                throw new BadRequestException(
                        "Invalid refresh token"
                );
            }

            RefreshToken storedToken =
                    refreshTokenRepository
                            .findByJtiForUpdate(jti)
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Invalid refresh token"
                                    )
                            );

            boolean invalid =
                    storedToken.getRevokedAt() != null
                            || storedToken.getExpiresAt()
                            .isBefore(
                                    LocalDateTime.now(ZoneOffset.UTC)
                            )
                            || !storedToken.getTokenHash()
                            .equals(hashToken(refreshToken))
                            || !storedToken.getUser().getId()
                            .equals(userId)
                            || !jwtService.isRefreshTokenValid(
                            refreshToken,
                            userId
                    );

            if (invalid) {
                throw new BadRequestException(
                        "Invalid or expired refresh token"
                );
            }

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found"
                                    )
                            );

            if (!user.isEmailVerified()) {
                throw new BadRequestException(
                        "Email is not verified"
                );
            }

            /*
             * Update the current session's last-used time.
             */
            storedToken.setLastUsedAt(
                    LocalDateTime.now(ZoneOffset.UTC)
            );

            /*
             * Generate a new access token.
             *
             * The existing refresh session remains valid.
             */
            String newAccessToken =
                    jwtService.generateAccessToken(user);

            return new AuthResponse(
                    newAccessToken,
                    refreshToken
            );

        } catch (JwtException | IllegalArgumentException e) {

            throw new BadRequestException(
                    "Invalid or expired refresh token"
            );
        }
    }


    @Transactional
    public void logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        try {

            String jti =
                    jwtService.getJtiFromRefreshToken(
                            refreshToken
                    );

            refreshTokenRepository
                    .findByJtiForUpdate(jti)
                    .ifPresent(token ->
                            token.setRevokedAt(
                                    LocalDateTime.now(ZoneOffset.UTC)
                            )
                    );

        } catch (JwtException | IllegalArgumentException ignored) {
            /*
             * Cookie will still be deleted by the controller.
             */
        }
    }


    private void createRefreshSession(
            User user,
            String refreshToken) {

        List<RefreshToken> activeSessions =
                refreshTokenRepository
                        .findByUserAndRevokedAtIsNull(user);

        /*
         * Remove expired sessions from consideration.
         */
        LocalDateTime now =
                LocalDateTime.now(ZoneOffset.UTC);

        activeSessions.removeIf(
                session -> session.getExpiresAt().isBefore(now)
        );

        /*
         * Maximum two active sessions.
         *
         * If a third login happens,
         * revoke the least recently used session.
         */
        if (activeSessions.size() >= SESSION_LIMIT) {

            RefreshToken oldestSession =
                    activeSessions.stream()
                            .min(
                                    Comparator.comparing(
                                            token -> token.getLastUsedAt()
                                    )
                            )
                            .orElseThrow();

            oldestSession.setRevokedAt(now);

            refreshTokenRepository.save(oldestSession);
        }

        String jti =
                jwtService.getJtiFromRefreshToken(
                        refreshToken
                );

        LocalDateTime expiresAt =
                LocalDateTime.ofInstant(
                        jwtService
                                .getRefreshExpiration(refreshToken)
                                .toInstant(),
                        ZoneOffset.UTC
                );

        RefreshToken refreshTokenEntity =
                RefreshToken.builder()
                        .jti(jti)
                        .tokenHash(
                                hashToken(refreshToken)
                        )
                        .user(user)
                        .expiresAt(expiresAt)
                        .lastUsedAt(now)
                        .build();

        refreshTokenRepository.save(
                refreshTokenEntity
        );
    }


    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    )
            );

        } catch (java.security.NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    e
            );
        }
    }


    public void verifyEmail(String token) {
        emailVerificationService.verifyEmail(token);
    }


    public void resendVerificationEmail(
            ResendVerificationEmailRequest request) {

        emailVerificationService
                .resendVerificationEmail(
                        request.getEmail()
                );
    }
}