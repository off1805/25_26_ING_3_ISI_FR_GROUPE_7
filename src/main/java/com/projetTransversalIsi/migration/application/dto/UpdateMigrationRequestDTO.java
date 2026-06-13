package com.projetTransversalIsi.migration.application.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateMigrationRequestDTO(
        @NotNull Long classeDestinationId
) {}
