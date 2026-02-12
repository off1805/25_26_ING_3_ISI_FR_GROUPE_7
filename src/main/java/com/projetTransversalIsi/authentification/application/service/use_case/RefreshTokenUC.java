package com.projetTransversalIsi.authentification.application.service.use_case;

import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;

public interface RefreshTokenUC {

    LoginResponseDTO execute(String rTokenId);
}
