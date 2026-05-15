package com.projetTransversalIsi.justificatif.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JustificatifAPViewDTO(
        Long id,
        Long etudiantId,
        String etudiantNom,
        String etudiantPrenom,
        String etudiantMatricule,
        List<SeanceInfo> seances,
        String motif,
        String message,
        List<FichierInfo> fichiers,
        LocalDate dateAbsence,
        String statut,
        String commentaireAP,
        LocalDateTime createdAt
) {
    public record SeanceInfo(Long seanceId, String date, String libelle) {}
    public record FichierInfo(String url, String nomOriginal) {}
}
