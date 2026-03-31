package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SeanceResponseDTO(
        Long id,
        String libelle,
        String salle,
        LocalDate dateSeance,
        LocalTime heureDebut,
        LocalTime heureFin,
        Long coursId,
        Long enseignantId,
        Seance.TypeSeance type,
        String couleur,
        String iconKey,
        boolean deleted,
        LocalDateTime deletedAt
) {
    public static SeanceResponseDTO fromDomain(Seance seance) {
        return new SeanceResponseDTO(
                seance.getId(),
                seance.getLibelle(),
                seance.getSalle(),
                seance.getDateSeance(),
                seance.getHeureDebut(),
                seance.getHeureFin(),
                seance.getCoursId(),
                seance.getEnseignantId(),
                seance.getType(),
                seance.getCouleur(),
                seance.getIconKey(),
                seance.isDeleted(),
                seance.getDeletedAt()
        );
    }
}
