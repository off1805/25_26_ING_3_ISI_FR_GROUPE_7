package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Représente la feuille de présence ouverte par un enseignant pour une séance donnée.
// Un seul PresenceList par seanceId est attendu en pratique (MarkStudentPresentUCImpl
// prend le premier de la liste). Une seanceId peut théoriquement en avoir plusieurs
// si l'enseignant rouvre une feuille, mais le code client ne gère pas ce cas multi-liste.
@Getter
@Setter
@NoArgsConstructor
public class PresenceList {

    private Long id;
    private Long seanceId;
    private Long classeId;
    private Long ueId;
    private Long enseignantId;
    // date de la séance, dupliquée ici pour faciliter les requêtes de reporting sans jointure.
    private LocalDate date;
    private LocalDateTime createdAt;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public PresenceList(Long seanceId, Long classeId, Long ueId, Long enseignantId, LocalDate date) {
        this.seanceId = seanceId;
        this.classeId = classeId;
        this.ueId = ueId;
        this.enseignantId = enseignantId;
        this.date = date;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    public void delete() {
        if (this.deleted) throw new IllegalStateException("Liste déjà supprimée");
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
