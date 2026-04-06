package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOffreUeRequestDTO(
        @NotNull Long ueId,
        @NotNull Long anneeScolaireId) {
}
