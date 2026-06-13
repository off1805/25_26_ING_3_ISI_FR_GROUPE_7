package com.projetTransversalIsi.migration.application.dto;

import jakarta.validation.constraints.NotNull;

public record CreateMigrationRequestDTO(
        @NotNull Long userId,
        @NotNull Long classeDestinationId
) {}
