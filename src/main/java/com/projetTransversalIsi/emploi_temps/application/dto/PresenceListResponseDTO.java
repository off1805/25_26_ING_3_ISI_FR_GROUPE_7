package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PresenceListResponseDTO(
        Long id,
        Long seanceId,
        Long classeId,
        Long ueId,
        Long enseignantId,
        LocalDate date,
        LocalDateTime createdAt,
        boolean deleted
) {
    public static PresenceListResponseDTO fromDomain(PresenceList p) {
        return new PresenceListResponseDTO(
                p.getId(), p.getSeanceId(), p.getClasseId(),
                p.getUeId(), p.getEnseignantId(), p.getDate(),
                p.getCreatedAt(), p.isDeleted()
        );
    }
}
