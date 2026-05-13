package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

// Enregistrement de présence d'un étudiant pour une plage horaire définie par un Appel.
// heureDebut/heureFin sont dénormalisés depuis l'Appel pour permettre des requêtes directes.
// etudiantId est dénormalisé pour permettre les requêtes par appel+étudiant sans jointure.
//
// Cycle de vie de isPresent :
//   null  → appel créé, statut de l'étudiant non encore déterminé
//   true  → étudiant marqué présent (via scan ou appel manuel)
//   false → appel clôturé, étudiant non présent (absent)
@Getter
@Setter
@NoArgsConstructor
public class InfoPresenceRow {

    private Long id;
    private Long etudiantId;
    private Long presenceRowId;  // null jusqu'à ce que l'étudiant soit évalué (présent ou absent)
    private Long appelId;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Nullable : null = non déterminé, true = présent, false = absent
    private Boolean isPresent;

    private LocalDateTime markedAt;

    // Constructeur pour état non-déterminé (appel créé, PresenceRow déjà liée, statut en attente).
    // presenceRowId est toujours fourni — on crée la PresenceRow avant l'InfoPresenceRow.
    public InfoPresenceRow(Long etudiantId, Long presenceRowId, Long appelId,
                           LocalTime heureDebut, LocalTime heureFin) {
        this.etudiantId = etudiantId;
        this.presenceRowId = presenceRowId;
        this.appelId = appelId;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.isPresent = null;
        this.markedAt = LocalDateTime.now();
    }

    // Constructeur pour état déterminé (présent ou absent).
    public InfoPresenceRow(Long etudiantId, Long presenceRowId, Long appelId,
                           LocalTime heureDebut, LocalTime heureFin, boolean isPresent) {
        this.etudiantId = etudiantId;
        this.presenceRowId = presenceRowId;
        this.appelId = appelId;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.isPresent = isPresent;
        this.markedAt = LocalDateTime.now();
    }
}
