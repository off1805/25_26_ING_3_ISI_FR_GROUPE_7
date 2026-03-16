package com.projetTransversalIsi.emploi_Temps.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateEmploiTempsWithSeancesDTO(
        @NotNull(message = "L'ID est obligatoire")
        Long id,

        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateFin,

        Integer semaine,

        @NotNull(message = "La classe est obligatoire")
        Long classeId,

        @NotEmpty(message = "La liste des séances est obligatoire")
        List<@Valid SeanceCreationDTO> seances
) {
    public record SeanceCreationDTO(
            @NotNull(message = "Le libellé de la séance est obligatoire")
            String libelle,

            @NotNull(message = "La salle est obligatoire")
            String salle,

            @NotNull(message = "La date de la séance est obligatoire")
            LocalDate dateSeance,

            @NotNull(message = "L'heure de début est obligatoire")
            LocalTime heureDebut,

            @NotNull(message = "L'heure de fin est obligatoire")
            LocalTime heureFin,

            @NotNull(message = "L'ID du cours est obligatoire")
            Long coursId,

            @NotNull(message = "L'ID de l'enseignant est obligatoire")
            Long enseignantId
    ) {}
}
