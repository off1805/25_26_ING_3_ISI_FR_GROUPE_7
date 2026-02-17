package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;


public interface LoginUC {
    LoginResponseDTO execute(LoginRequestDTO command);
}
