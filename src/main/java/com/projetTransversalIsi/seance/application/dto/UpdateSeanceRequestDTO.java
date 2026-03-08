package com.projetTransversalIsi.seance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record UpdateSeanceRequestDTO(

        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime heureDebut,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime heureFin,

        @NotBlank(message = "La salle est obligatoire")
        String salle
) {}