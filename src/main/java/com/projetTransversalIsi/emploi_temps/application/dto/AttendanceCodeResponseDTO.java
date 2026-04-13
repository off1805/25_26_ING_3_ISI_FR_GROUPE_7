package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;

import java.time.LocalDateTime;

public record AttendanceCodeResponseDTO(
        Long id,
        Long seanceId,
        Long enseignantId,
        AttendanceCode.CodeType type,
        String valeur,
        String scanUrl,
        float heuresAMarquer,
        int dureeVieMinutes,
        LocalDateTime createdAt,
        boolean expired
) {
    private static final String BASE_URL = "http://localhost:8080/api/presences/scan?code=";

    public static AttendanceCodeResponseDTO fromDomain(AttendanceCode c) {
        String scanUrl = c.getType() == AttendanceCode.CodeType.QR
                ? BASE_URL + c.getValeur()
                : null;
        return new AttendanceCodeResponseDTO(
                c.getId(), c.getSeanceId(), c.getEnseignantId(),
                c.getType(), c.getValeur(), scanUrl,
                c.getHeuresAMarquer(), c.getDureeVieMinutes(),
                c.getCreatedAt(), c.isExpired()
        );
    }
}
