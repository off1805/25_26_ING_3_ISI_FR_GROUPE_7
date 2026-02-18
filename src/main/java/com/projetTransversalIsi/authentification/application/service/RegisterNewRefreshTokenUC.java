package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.user.domain.User;

public interface RegisterNewRefreshTokenUC {
    RefreshToken execute(User user);
}
