package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.domain.User;

@FunctionalInterface
public interface FindUserByIdUC {
    User execute(Long userId);
}