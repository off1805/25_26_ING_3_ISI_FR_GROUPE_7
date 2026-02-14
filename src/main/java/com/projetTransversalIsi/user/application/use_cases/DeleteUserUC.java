package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.application.dto.DeleteUserRequestDTO;


public interface DeleteUserUC {
    void execute(DeleteUserRequestDTO command);
}
