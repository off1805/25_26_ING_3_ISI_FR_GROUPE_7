package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateSemestreRequestDTO(
        @NotNull
        @Min(1)
        @Max(2)
        Integer numero,

        LocalDate dateDebut,
        
        LocalDate dateFin,

        @NotNull
        Long anneeScolaireId,

        @NotNull
        Long niveauId
) {
}