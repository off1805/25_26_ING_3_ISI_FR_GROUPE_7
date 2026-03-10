package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.dto.DeleteUserRequestDTO;


public interface DeleteUserUC {
    void execute(DeleteUserRequestDTO command);
}
