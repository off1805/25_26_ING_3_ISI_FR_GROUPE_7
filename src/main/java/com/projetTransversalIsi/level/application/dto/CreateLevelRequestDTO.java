package com.projetTransversalIsi.level.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLevelRequestDTO(
        @NotBlank String nom,
        String description
) {
}
