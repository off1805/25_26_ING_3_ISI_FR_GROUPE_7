package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.NotNull;

public record AssignEnseignantDTO(
        @NotNull Long enseignantId,
        @NotNull Long classeId) {
}
