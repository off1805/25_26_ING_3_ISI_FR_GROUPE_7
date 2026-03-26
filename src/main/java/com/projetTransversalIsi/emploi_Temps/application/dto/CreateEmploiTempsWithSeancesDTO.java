package com.projetTransversalIsi.emploi_Temps.application.dto;

import com.projetTransversalIsi.seance.domain.Seance;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateEmploiTempsWithSeancesDTO(

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

            // Nullable pour les événements
            String salle,

            @NotNull(message = "La date de la séance est obligatoire")
            LocalDate dateSeance,

            @NotNull(message = "L'heure de début est obligatoire")
            LocalTime heureDebut,

            @NotNull(message = "L'heure de fin est obligatoire")
            LocalTime heureFin,

            // Nullable pour les événements
            Long coursId,

            // Nullable pour les événements
            Long enseignantId,

            // SEANCE ou EVENEMENT (défaut: SEANCE si null)
            Seance.TypeSeance type,

            // Couleur hex ou id de palette (ex: "violet", "#7c3aed")
            String couleur,

            // Clé d'icône (ex: "pause", "event", "perso")
            String iconKey
    ) {
        public Seance.TypeSeance resolvedType() {
            return type != null ? type : Seance.TypeSeance.SEANCE;
        }
    }
}
