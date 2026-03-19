package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.core.config.utils.JwtUtil;
import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create and persist a new refresh token for a user
     */
    public RefreshToken createRefreshToken(User user) {
        // Revoke any existing valid refresh token for this user
        refreshTokenRepository.findByUserAndRevokedFalse(user).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtUtil.generateRefreshToken(user.getUsername()))
                .expiryDate(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpiryMs()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validate a refresh token
     */
    public Optional<RefreshToken> validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> !rt.getRevoked())
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

    /**
     * Revoke a refresh token
     */
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    /**
     * Delete all refresh tokens for a user (logout)
     */
    public void deleteUserRefreshTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}


