package com.kushal.hireflow.auth.repository;

import com.kushal.hireflow.auth.entity.EmailVerificationToken;
import com.kushal.hireflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
