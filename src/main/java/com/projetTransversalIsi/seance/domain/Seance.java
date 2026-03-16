package com.projetTransversalIsi.seance.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Seance {

    private Long id;
    private String libelle;
    private String salle;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Long coursId;
    private Long enseignantId;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public Seance(String libelle, String salle, LocalDate dateSeance, LocalTime heureDebut,
                  LocalTime heureFin, Long coursId, Long enseignantId) {
        this.libelle = libelle;
        this.salle = salle;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.coursId = coursId;
        this.enseignantId = enseignantId;
        this.deleted = false;
    }


    public void update(LocalTime heureDebut, LocalTime heureFin) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public void delete() {
        if (this.deleted) {
            throw new IllegalStateException("La séance " + this.id + " est déjà supprimée");
        }
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        if (!this.deleted) {
            throw new IllegalStateException("La séance " + this.id + " n'est pas supprimée");
        }
        this.deleted = false;
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
