package com.projetTransversalIsi.emploi_temps.domain.model;

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

    public enum TypeSeance {
        SEANCE, EVENEMENT
    }

    private Long id;
    private String libelle;
    private String salle;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Nullable pour les événements
    private Long coursId;
    private Long enseignantId;

    // Nouveaux champs pour supporter les événements
    private TypeSeance type = TypeSeance.SEANCE;
    private String couleur;
    private String iconKey;

    private boolean deleted = false;
    private LocalDateTime deletedAt;

    /** Constructeur pour une séance de cours (usage existant) */
    public Seance(String libelle, String salle, LocalDate dateSeance, LocalTime heureDebut,
                  LocalTime heureFin, Long coursId, Long enseignantId) {
        this.libelle = libelle;
        this.salle = salle;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.coursId = coursId;
        this.enseignantId = enseignantId;
        this.type = TypeSeance.SEANCE;
        this.deleted = false;
    }

    /** Constructeur pour un événement (pas de cours ni d'enseignant) */
    public Seance(String libelle, LocalDate dateSeance, LocalTime heureDebut,
                  LocalTime heureFin, String couleur, String iconKey) {
        this.libelle = libelle;
        this.salle = null;
        this.dateSeance = dateSeance;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.coursId = null;
        this.enseignantId = null;
        this.type = TypeSeance.EVENEMENT;
        this.couleur = couleur;
        this.iconKey = iconKey;
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

    public boolean isEvenement() {
        return TypeSeance.EVENEMENT.equals(this.type);
    }
}
