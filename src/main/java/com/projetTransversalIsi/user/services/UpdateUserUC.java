package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.dto.UpdateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;

public interface UpdateUserUC {
    User execute(Long id, UpdateUserRequestDTO command);
}
