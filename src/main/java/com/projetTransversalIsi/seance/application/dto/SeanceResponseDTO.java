package com.projetTransversalIsi.seance.application.dto;

import com.projetTransversalIsi.seance.domain.Seance;
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
                seance.isDeleted(),
                seance.getDeletedAt()
        );
    }
}
