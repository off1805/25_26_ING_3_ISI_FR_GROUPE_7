package com.projetTransversalIsi.user.application.services;


import com.projetTransversalIsi.user.domain.User;

import java.util.Optional;


public interface DefaultUserService {

    Optional<User> findUserById(Long id);

}
