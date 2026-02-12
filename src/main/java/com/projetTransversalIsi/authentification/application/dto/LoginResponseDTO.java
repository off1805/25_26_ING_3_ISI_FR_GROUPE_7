package com.projetTransversalIsi.authentification.application.dto;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDTO {
    private String token;
    private String refreshToken;
}
