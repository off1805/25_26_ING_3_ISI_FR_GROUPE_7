package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record InfoPresenceRowResponseDTO(
        Long id,
        Long etudiantId,
        Long presenceRowId,
        Long appelId,
        LocalTime heureDebut,
        LocalTime heureFin,
        Boolean isPresent,
        LocalDateTime markedAt
) {
    public static InfoPresenceRowResponseDTO fromDomain(InfoPresenceRow r) {
        return new InfoPresenceRowResponseDTO(
                r.getId(), r.getEtudiantId(), r.getPresenceRowId(), r.getAppelId(),
                r.getHeureDebut(), r.getHeureFin(), r.getIsPresent(), r.getMarkedAt()
        );
    }
}
