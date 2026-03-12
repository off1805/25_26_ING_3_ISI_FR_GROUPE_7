package com.projetTransversalIsi.authentification.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDTO {

    @NotBlank
    public String refreshToken;

}
