package com.urke.saasbackendstarter.repository;

import com.urke.saasbackendstarter.domain.RefreshToken;
import com.urke.saasbackendstarter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
