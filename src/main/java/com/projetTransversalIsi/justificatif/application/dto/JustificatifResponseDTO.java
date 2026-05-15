package com.projetTransversalIsi.justificatif.application.dto;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.model.JustificatifFichier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record JustificatifResponseDTO(
        Long id,
        Long etudiantId,
        List<Long> seanceIds,
        String motif,
        String message,
        List<String> fichierUrls,
        LocalDate dateAbsence,
        String statut,
        String commentaireAP,
        LocalDateTime createdAt
) {
    public static JustificatifResponseDTO fromDomain(Justificatif j) {
        List<String> urls = j.getFichiers().stream()
                .map(JustificatifFichier::getFichierUrl)
                .collect(Collectors.toList());
        return new JustificatifResponseDTO(
                j.getId(), j.getEtudiantId(), j.getSeanceIds(),
                j.getMotif(), j.getMessage(), urls, j.getDateAbsence(),
                j.getStatut().name(), j.getCommentaireAP(), j.getCreatedAt()
        );
    }
}
