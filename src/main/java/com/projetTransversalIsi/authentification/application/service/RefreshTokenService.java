package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenService implements DefaultRefreshTokenService {
    private final RefreshTokenRepository rTokenRepo;
    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public Optional<RefreshToken> findRefreshTokenById(String id){
       return rTokenRepo.getRefreshTokenById(id);
    }

    @Override
   public RefreshToken registerNewRTokenForUser(User user){
        RefreshToken refreshToken= new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(new Date());
        refreshToken.setExpiresAt(new Date(System.currentTimeMillis()+refreshTokenDurationMs));
        return rTokenRepo.saveRefreshToken(refreshToken);
   }
}
