package com.projetTransversalIsi.Filiere.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateFiliereRequestDTO(
        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotBlank(message = "Le code est obligatoire")
        @Size(min = 2, max = 10, message = "Le code doit faire entre 2 et 10 caractères")
        String code,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 3, max = 100, message = "Le nom doit faire entre 3 et 100 caractères")
        String nom,

        String description
) {
}
