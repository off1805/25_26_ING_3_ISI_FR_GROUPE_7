package com.projetTransversalIsi.emploi_temps.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSeanceRequestDTO(
        @NotNull(message = "Le libellé est obligatoire")
        String libelle,
        String salle,
        @NotNull(message = "La date est obligatoire")
        LocalDate dateSeance,
        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime heureDebut,
        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime heureFin,
        Long coursId,
        Long enseignantId
) {}
