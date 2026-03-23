package com.projetTransversalIsi.ue.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateUeRequestDTO(
        @NotBlank String libelle,
        @NotBlank String code,
        @NotNull @Min(1) int credit,
        @NotNull @Min(1) int volumeHoraireTotal,
        String description,
        String couleur,
        @NotNull Long specialiteId,
        Set<Long> enseignantIds) {
}
