package com.projetTransversalIsi.ue.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUeRequestDTO(
        @NotBlank String libelle,
        @NotBlank String code,
        @NotNull @Min(1) int credit,
        @NotNull @Min(1) int volumeHoraireTotal,
        String description,
        String couleur,
        @NotNull Long specialiteId) {
}
