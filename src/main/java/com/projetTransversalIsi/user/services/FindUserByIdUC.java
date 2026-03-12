package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.domain.User;

@FunctionalInterface
public interface FindUserByIdUC {
    User execute(Long userId);
}