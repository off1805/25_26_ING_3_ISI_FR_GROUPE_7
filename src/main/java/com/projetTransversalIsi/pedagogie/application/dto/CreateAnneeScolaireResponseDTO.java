package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAnneeScolaireResponseDTO(
        Long id,
        int anneeDebut,
        int anneeFin,
        boolean active
) {
}
