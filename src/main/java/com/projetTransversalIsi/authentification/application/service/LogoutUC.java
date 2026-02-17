package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;

public interface LogoutUC {
    void execute(RefreshRequestDTO command);
}
