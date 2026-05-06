package com.projetTransversalIsi.justificatif.application.dto;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JustificatifResponseDTO(
        Long id,
        Long etudiantId,
        Long seanceId,
        String motif,
        String fichierUrl,
        LocalDate dateAbsence,
        String statut,
        String commentaireAP,
        LocalDateTime createdAt
) {
    public static JustificatifResponseDTO fromDomain(Justificatif j) {
        return new JustificatifResponseDTO(
                j.getId(), j.getEtudiantId(), j.getSeanceId(),
                j.getMotif(), j.getFichierUrl(), j.getDateAbsence(),
                j.getStatut().name(), j.getCommentaireAP(), j.getCreatedAt()
        );
    }
}
