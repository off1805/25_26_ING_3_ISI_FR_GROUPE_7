package com.projetTransversalIsi.structure_academique.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSchoolRequestDTO(
        @NotBlank @Size(max = 255) String name,
        double latitude,
        double longitude,
        double rayon
) {}
