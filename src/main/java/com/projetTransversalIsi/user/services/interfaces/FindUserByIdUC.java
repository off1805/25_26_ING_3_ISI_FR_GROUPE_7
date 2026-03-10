package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.domain.User;

@FunctionalInterface
public interface FindUserByIdUC {
    User execute(Long userId);
}