package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSemestreRequestDTO(
        @NotNull
        @Min(1)
        @Max(2)
        Integer numero,

        @NotNull
        Long anneeScolaireId,

        @NotNull
        Long specialiteId
) {
}