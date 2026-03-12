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
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Long coursId;
    private Long enseignantId;
    private String salle;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public Seance(String libelle, LocalDate dateSeance, LocalTime heureDebut,
                  LocalTime heureFin, Long coursId, Long enseignantId, String salle) {
        this.libelle = libelle;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.coursId = coursId;
        this.enseignantId = enseignantId;
        this.salle = salle;
        this.deleted = false;
    }


    public void update(String libelle, LocalTime heureDebut, LocalTime heureFin, String salle) {
        this.libelle = libelle;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.salle = salle;
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