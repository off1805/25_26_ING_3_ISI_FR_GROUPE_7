package com.projetTransversalIsi.Niveau.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateNiveauRequestDTO(
        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotNull(message = "L'ordre est obligatoire")
        @Min(value = 1, message = "L'ordre doit être au moins 1")
        int ordre,

        String description,

        @NotNull(message = "L'ID de la filière est obligatoire")
        Long filiereId
) {
}
