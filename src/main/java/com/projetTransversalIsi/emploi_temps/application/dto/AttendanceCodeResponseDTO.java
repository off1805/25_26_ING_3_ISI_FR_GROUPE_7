package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;

import java.time.LocalDateTime;

public record AttendanceCodeResponseDTO(
        Long id,
        Long seanceId,
        Long enseignantId,
        AttendanceCode.CodeType type,
        String valeur,
        float heuresAMarquer,
        int dureeVieMinutes,
        LocalDateTime createdAt,
        boolean expired
) {
    public static AttendanceCodeResponseDTO fromDomain(AttendanceCode c) {
        return new AttendanceCodeResponseDTO(
                c.getId(), c.getSeanceId(), c.getEnseignantId(),
                c.getType(), c.getValeur(), c.getHeuresAMarquer(),
                c.getDureeVieMinutes(), c.getCreatedAt(), c.isExpired()
        );
    }
}
