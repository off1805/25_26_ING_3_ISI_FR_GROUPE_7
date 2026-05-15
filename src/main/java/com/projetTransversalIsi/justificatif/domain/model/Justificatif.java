package com.projetTransversalIsi.justificatif.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Justificatif {

    public enum Statut { PENDING, APPROVED, REJECTED }

    private Long id;
    private Long etudiantId;

    // Un justificatif peut couvrir plusieurs séances (ex. certificat médical multi-jours).
    private List<Long> seanceIds = new ArrayList<>();

    private String motif;
    private String message;

    // Pièces justificatives — chacune a son URL et son nom d'origine.
    private List<JustificatifFichier> fichiers = new ArrayList<>();

    private LocalDate dateAbsence;
    private Statut statut = Statut.PENDING;
    private String commentaireAP;
    private LocalDateTime createdAt;

    public Justificatif(Long etudiantId, List<Long> seanceIds, String motif, String message,
                        List<JustificatifFichier> fichiers, LocalDate dateAbsence) {
        this.etudiantId = etudiantId;
        this.seanceIds  = seanceIds != null ? seanceIds : new ArrayList<>();
        this.motif      = motif;
        this.message    = message;
        this.fichiers   = fichiers != null ? fichiers : new ArrayList<>();
        this.dateAbsence = dateAbsence;
        this.statut     = Statut.PENDING;
        this.createdAt  = LocalDateTime.now();
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
