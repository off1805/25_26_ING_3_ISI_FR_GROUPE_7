package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.domain.User;

import java.util.Optional;

public interface GetUserByEmail {
    Optional<User> getByEmail(String email);
}
