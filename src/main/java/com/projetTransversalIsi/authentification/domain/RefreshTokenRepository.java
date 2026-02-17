package com.projetTransversalIsi.authentification.domain;

import com.projetTransversalIsi.user.domain.User;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> getRefreshTokenById(String id);
    RefreshToken saveRefreshToken(RefreshToken rToken);

}
