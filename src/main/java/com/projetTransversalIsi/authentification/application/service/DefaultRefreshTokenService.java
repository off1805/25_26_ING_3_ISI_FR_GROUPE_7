package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.user.domain.User;

import java.util.Optional;

public interface DefaultRefreshTokenService {
    Optional<RefreshToken> findRefreshTokenById(String id);
    RefreshToken registerNewRTokenForUser(User user);
}
