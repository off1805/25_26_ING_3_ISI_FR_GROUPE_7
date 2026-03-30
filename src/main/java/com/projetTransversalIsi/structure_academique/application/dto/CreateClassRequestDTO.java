package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClassRequestDTO(
        @NotBlank String code,
        String description,
        @NotNull Long specialiteId
) {}
