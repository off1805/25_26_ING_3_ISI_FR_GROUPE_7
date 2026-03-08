package com.projetTransversalIsi.seance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSeanceRequestDTO(

        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull(message = "La date est obligatoire")
        LocalDate dateSeance,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime heureDebut,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime heureFin,

        @NotNull(message = "L'ID du cours est obligatoire")
        Long coursId,

        @NotNull(message = "L'ID de l'enseignant est obligatoire")
        Long enseignantId,

        @NotBlank(message = "La salle est obligatoire")
        String salle
) {}