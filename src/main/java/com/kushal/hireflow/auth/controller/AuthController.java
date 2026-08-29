package com.kushal.hireflow.auth.controller;

import com.kushal.hireflow.auth.dto.AccessTokenResponse;
import com.kushal.hireflow.auth.dto.AuthResponse;
import com.kushal.hireflow.auth.dto.LoginRequest;
import com.kushal.hireflow.auth.dto.RegisterRequest;
import com.kushal.hireflow.auth.dto.ResendVerificationEmailRequest;
import com.kushal.hireflow.auth.service.AuthService;
import com.kushal.hireflow.common.response.MessageResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";

    private final AuthService authService;

    @Value("${jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-cookie-same-site:Strict}")
    private String refreshCookieSameSite;

    @Value("${jwt.refresh-cookie-path:/api/auth}")
    private String refreshCookiePath;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpServletResponse) {

        AuthResponse authResponse = authService.login(request);

        addRefreshTokenCookie(httpServletResponse, authResponse.getRefreshToken());

        return ResponseEntity.ok(
                new AccessTokenResponse(authResponse.getAccessToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = getRefreshTokenFromCookie(request);
        AuthResponse authResponse = authService.refreshToken(refreshToken);

        addRefreshTokenCookie(response, authResponse.getRefreshToken());

        return ResponseEntity.ok(
                new AccessTokenResponse(authResponse.getAccessToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        authService.logout(getRefreshTokenFromCookie(request));
        deleteRefreshTokenCookie(response);

        return ResponseEntity.ok(
                new MessageResponse("Logged out successfully")
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(
            @RequestParam(required = false) String token) {

        authService.verifyEmail(token);
        return ResponseEntity.ok(
                new MessageResponse("Email verified successfully")
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationEmailRequest request) {

        authService.resendVerificationEmail(request);
        return ResponseEntity.ok(
                new MessageResponse("Verification email sent successfully")
        );
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void addRefreshTokenCookie(
            HttpServletResponse response,
            String refreshToken) {

        ResponseCookie cookie = ResponseCookie.from(
                        REFRESH_COOKIE_NAME,
                        refreshToken
                )
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(refreshExpiration / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(
                        REFRESH_COOKIE_NAME,
                        ""
                )
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
