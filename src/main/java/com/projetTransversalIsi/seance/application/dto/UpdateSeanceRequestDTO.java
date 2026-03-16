package com.projetTransversalIsi.seance.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record UpdateSeanceRequestDTO(

        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime heureDebut,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime heureFin
) {}
