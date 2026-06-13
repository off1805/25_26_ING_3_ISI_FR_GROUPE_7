package com.projetTransversalIsi.pedagogie.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AssignEnseignantClassesDTO(
        @NotNull Long enseignantId,
        @NotEmpty Set<Long> classeIds) {
}
