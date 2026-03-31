package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFiliereRequestDTO(
        @NotBlank @Size(min = 2, max = 100) String nom,
        String description,
        @NotNull Long cycleId
) {}
