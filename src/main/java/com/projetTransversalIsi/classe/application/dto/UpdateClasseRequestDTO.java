package com.projetTransversalIsi.classe.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateClasseRequestDTO(
        @NotBlank String code,
        String description,
        @NotNull Long specialiteId
) {
}
