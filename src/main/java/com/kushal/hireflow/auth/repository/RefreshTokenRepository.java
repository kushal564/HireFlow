package com.kushal.hireflow.auth.repository;

import com.kushal.hireflow.auth.entity.RefreshToken;
import com.kushal.hireflow.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RefreshToken r where r.jti = :jti")
    Optional<RefreshToken> findByJtiForUpdate(@Param("jti") String jti);

    List<RefreshToken> findByUserAndRevokedAtIsNull(User user);

    void deleteByExpiresAtBefore(LocalDateTime time);
}