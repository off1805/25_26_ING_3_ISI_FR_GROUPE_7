package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.domain.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Default expiration: 24 hours (can be adjusted as needed)
    private static final long REFRESH_TOKEN_DURATION_MS = 24 * 60 * 60 * 1000L;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken registerNewRTokenForUser(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(new Date());
        refreshToken.setExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION_MS));

        return refreshTokenRepository.saveRefreshToken(refreshToken);
    }

    public Optional<RefreshToken> findRefreshTokenById(String token) {
        return refreshTokenRepository.getRefreshTokenById(token);
    }
}
