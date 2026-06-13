package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InfoRetardRowResponseDTO(
        Long id,
        Long etudiantId,
        Long retardRowId,
        int jourSemaine,
        LocalDate date,
        boolean enRetard,
        LocalDateTime markedAt
) {
    public static InfoRetardRowResponseDTO fromDomain(InfoRetardRow r) {
        return new InfoRetardRowResponseDTO(
                r.getId(), r.getEtudiantId(), r.getRetardRowId(),
                r.getJourSemaine(), r.getDate(), r.isEnRetard(), r.getMarkedAt()
        );
    }
}
