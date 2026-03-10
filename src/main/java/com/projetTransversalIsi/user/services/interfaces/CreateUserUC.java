package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;


public interface CreateUserUC {
    User execute(CreateUserRequestDTO command);
}
