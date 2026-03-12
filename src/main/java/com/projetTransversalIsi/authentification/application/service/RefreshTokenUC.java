package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;

public interface RefreshTokenUC {

    LoginResponseDTO execute(RefreshRequestDTO command);
}
