package com.projetTransversalIsi.cycle.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCycleRequestDTO(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 2, max = 20) String code,
        @Min(1) int durationYears,
        String description
) {}
