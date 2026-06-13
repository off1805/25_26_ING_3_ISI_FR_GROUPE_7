package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCycleRequestDTO(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 1, max = 20) String code, // ← BUGFIX #2 : champ code ajouté
        @Min(1) int durationYears,
        String description
) {}
