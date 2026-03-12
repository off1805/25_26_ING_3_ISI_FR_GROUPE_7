package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.dto.DeleteUserRequestDTO;


public interface DeleteUserUC {
    void execute(DeleteUserRequestDTO command);
}
