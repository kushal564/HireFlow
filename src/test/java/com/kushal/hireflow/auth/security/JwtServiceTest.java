package com.kushal.hireflow.auth.security;

import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.Role;
import com.kushal.hireflow.user.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "accessSecret",
                "HireFlowTestAccessSecret_2026_123456789"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "refreshSecret",
                "HireFlowTestRefreshSecret_2026_987654321"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "accessExpiration",
                900000L
        );

        ReflectionTestUtils.setField(
                jwtService,
                "refreshExpiration",
                2592000000L
        );

        user = mock(User.class);

        Role role = mock(Role.class);

        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("candidate@test.com");
        when(user.getRole()).thenReturn(role);
        when(role.getName()).thenReturn(RoleName.CANDIDATE);
    }


    @Test
    void shouldGenerateAccessTokenWithCorrectUserId() {

        String token = jwtService.generateAccessToken(user);

        Long userId =
                jwtService.getUserIdFromAccessToken(token);

        assertEquals(1L, userId);
    }


    @Test
    void shouldValidateAccessTokenForCorrectUser() {

        String token = jwtService.generateAccessToken(user);

        boolean valid =
                jwtService.isAccessTokenValid(token, 1L);

        assertTrue(valid);
    }


    @Test
    void shouldRejectAccessTokenForDifferentUser() {

        String token = jwtService.generateAccessToken(user);

        boolean valid =
                jwtService.isAccessTokenValid(token, 2L);

        assertFalse(valid);
    }


    @Test
    void shouldRejectTamperedAccessToken() {

        String token = jwtService.generateAccessToken(user);

        String tamperedToken = token + "tampered";

        assertThrows(
                JwtException.class,
                () -> jwtService.isAccessTokenValid(
                        tamperedToken,
                        1L
                )
        );
    }


    @Test
    void shouldGenerateRefreshTokenWithCorrectUserId() {

        String token = jwtService.generateRefreshToken(user);

        Long userId =
                jwtService.getUserIdFromRefreshToken(token);

        assertEquals(1L, userId);
    }


    @Test
    void shouldGenerateRefreshTokenWithJti() {

        String token = jwtService.generateRefreshToken(user);

        String jti =
                jwtService.getJtiFromRefreshToken(token);

        assertNotNull(jti);
        assertFalse(jti.isBlank());
    }


    @Test
    void shouldValidateRefreshTokenForCorrectUser() {

        String token = jwtService.generateRefreshToken(user);

        boolean valid =
                jwtService.isRefreshTokenValid(token, 1L);

        assertTrue(valid);
    }


    @Test
    void shouldRejectTamperedRefreshToken() {

        String token = jwtService.generateRefreshToken(user);

        String tamperedToken = token + "tampered";

        assertThrows(
                JwtException.class,
                () -> jwtService.isRefreshTokenValid(
                        tamperedToken,
                        1L
                )
        );
    }
}