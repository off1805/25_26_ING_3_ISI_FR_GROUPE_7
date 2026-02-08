package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;


public interface CreateUserUC {
    User execute(CreateUserRequestDTO command);
}
