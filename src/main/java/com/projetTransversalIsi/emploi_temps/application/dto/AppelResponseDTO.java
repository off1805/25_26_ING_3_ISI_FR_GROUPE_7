package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.Appel;

import java.time.LocalDateTime;
import java.time.LocalTime;

// scanUrl : non-null uniquement pour QR (baseUrl + valeur UUID).
// expired : calculé à la lecture via Appel.isExpired() ; toujours false pour MANUEL.
public record AppelResponseDTO(
        Long id,
        Long presenceListId,
        Long enseignantId,
        Appel.TypeAppel typeAppel,
        String valeur,
        String scanUrl,
        Long attendanceCodeId,
        LocalTime heureDebut,
        LocalTime heureFin,
        int dureeVieMinutes,
        LocalDateTime createdAt,
        boolean expired
) {
    public static AppelResponseDTO fromDomain(Appel a, String baseUrl) {
        String scanUrl = a.getTypeAppel() == Appel.TypeAppel.QR && a.getValeur() != null
                ? baseUrl + a.getValeur()
                : null;
        return new AppelResponseDTO(
                a.getId(), a.getPresenceListId(), a.getEnseignantId(),
                a.getTypeAppel(), a.getValeur(), scanUrl, a.getAttendanceCodeId(),
                a.getHeureDebut(), a.getHeureFin(), a.getDureeVieMinutes(),
                a.getCreatedAt(), a.isExpired()
        );
    }
}
