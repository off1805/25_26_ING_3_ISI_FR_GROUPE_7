package com.projetTransversalIsi.justificatif.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Justificatif {

    public enum Statut { PENDING, APPROVED, REJECTED }

    private Long id;
    private Long etudiantId;
    private Long seanceId;
    private String motif;
    private String fichierUrl;
    private LocalDate dateAbsence;
    private Statut statut = Statut.PENDING;
    private String commentaireAP;
    private LocalDateTime createdAt;

    public Justificatif(Long etudiantId, Long seanceId, String motif,
                        String fichierUrl, LocalDate dateAbsence) {
        this.etudiantId = etudiantId;
        this.seanceId = seanceId;
        this.motif = motif;
        this.fichierUrl = fichierUrl;
        this.dateAbsence = dateAbsence;
        this.statut = Statut.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approuver(String commentaire) {
        this.statut = Statut.APPROVED;
        this.commentaireAP = commentaire;
    }

    public void rejeter(String commentaire) {
        this.statut = Statut.REJECTED;
        this.commentaireAP = commentaire;
    }
}
