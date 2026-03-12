package com.projetTransversalIsi.specialite.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSpecialiteRequestDTO(
        @NotBlank String code,
        @NotBlank String libelle,
        String description,
        @NotNull Long niveauId
) {}
