package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSpecialiteRequestDTO(
        @NotBlank String libelle,
        String description,
        @NotNull Long niveauId
) {}
