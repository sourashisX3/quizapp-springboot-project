package com.sourashis.quizapp.modules.auth.repository;

import com.sourashis.quizapp.modules.auth.entity.RefreshToken;
import com.sourashis.quizapp.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);

    void deleteByUserAndRevokedTrue(User user);
}

