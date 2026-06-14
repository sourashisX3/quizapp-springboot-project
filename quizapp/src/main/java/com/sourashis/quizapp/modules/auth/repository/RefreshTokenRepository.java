package com.sourashis.quizapp.modules.auth.repository;

import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    Optional<RefreshToken> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);
}
