package com.kushal.hireflow.auth.service;

import com.kushal.hireflow.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredRefreshTokens() {

        refreshTokenRepository.deleteByExpiresAtBefore(
                LocalDateTime.now(ZoneOffset.UTC)
        );
    }
}