package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateClasseRequestDTO(
        @NotBlank String code,
        String description
) {}
