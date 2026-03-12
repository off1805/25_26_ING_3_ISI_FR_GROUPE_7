package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;


public interface CreateUserUC {
    User execute(CreateUserRequestDTO command);
}
