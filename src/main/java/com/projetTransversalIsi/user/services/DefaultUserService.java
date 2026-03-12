package com.projetTransversalIsi.user.services;


import com.projetTransversalIsi.user.domain.User;

import java.util.Optional;


public interface DefaultUserService {

    Optional<User> findUserById(Long id);

    void deleteUser(long userId);
}
