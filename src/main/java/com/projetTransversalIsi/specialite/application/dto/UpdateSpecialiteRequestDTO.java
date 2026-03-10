package com.projetTransversalIsi.specialite.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSpecialiteRequestDTO(
        @NotBlank String libelle,
        String description,
        @NotBlank String brancheCode,
        @NotNull @Min(1) int niveauMinimum
) {}
