package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;

import java.time.LocalDateTime;

// DTO de réponse pour un code de présence.
// scanUrl : non-null uniquement pour le type QR (= baseUrl + valeur UUID).
//           null pour PIN car l'étudiant saisit le code manuellement, pas via une URL.
// expired : calculé à la lecture via AttendanceCode.isExpired() ; pas persisté en base.
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
    public static AttendanceCodeResponseDTO fromDomain(AttendanceCode c, String baseUrl) {
        // Construction de l'URL de scan uniquement pour QR.
        String scanUrl = c.getType() == AttendanceCode.CodeType.QR
                ? baseUrl + c.getValeur()
                : null;
        return new AttendanceCodeResponseDTO(
                c.getId(), c.getSeanceId(), c.getEnseignantId(),
                c.getType(), c.getValeur(), scanUrl,
                c.getHeuresAMarquer(), c.getDureeVieMinutes(),
                c.getCreatedAt(), c.isExpired()
        );
    }
}
