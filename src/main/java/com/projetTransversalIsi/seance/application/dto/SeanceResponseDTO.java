package com.projetTransversalIsi.seance.application.dto;

import com.projetTransversalIsi.seance.domain.Seance;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SeanceResponseDTO(
        Long id,
        String libelle,
        LocalDate dateSeance,
        LocalTime heureDebut,
        LocalTime heureFin,
        Long coursId,
        Long enseignantId,
        String salle,
        boolean deleted,
        LocalDateTime deletedAt
) {
    public static SeanceResponseDTO fromDomain(Seance seance) {
        return new SeanceResponseDTO(
                seance.getId(),
                seance.getLibelle(),
                seance.getDateSeance(),
                seance.getHeureDebut(),
                seance.getHeureFin(),
                seance.getCoursId(),
                seance.getEnseignantId(),
                seance.getSalle(),
                seance.isDeleted(),
                seance.getDeletedAt()
        );
    }
}