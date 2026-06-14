package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Entité de domaine représentant un créneau dans un emploi du temps.
// Le type discrimine deux comportements : SEANCE (cours avec enseignant/salle/cours)
// et EVENEMENT (entrée calendaire visuelle sans contrainte d'enseignant).
// La détection de conflit côté service ne s'applique qu'aux SEANCE (enseignantId non null).
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
    private Long anneeScolaireId;
    private LocalDate dateSeance;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Null pour les EVENEMENT — la vérification de conflit horaire ignore ces lignes.
    private Long coursId;
    private Long enseignantId;

    // type, couleur, iconKey ajoutés pour supporter les événements visuels dans le calendrier
    // sans modifier le schéma des séances de cours existantes.
    private TypeSeance type = TypeSeance.SEANCE;
    private String couleur;
    private String iconKey;

    private boolean deleted = false;
    private LocalDateTime deletedAt;

    // Constructeur SEANCE : enseignantId requis pour la détection de conflit horaire.
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

    // Constructeur EVENEMENT : salle/cours/enseignant laissés null intentionnellement ;
    // couleur et iconKey permettent l'affichage personnalisé dans le calendrier frontend.
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

    // update ne modifie que les horaires : la date et la salle sont fixées à la création
    // pour ne pas invalider les listes de présence déjà ouvertes sur cette séance.
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

    // Prédicat utilitaire utilisé dans GetCurrentSeanceUCImpl pour filtrer sans cast.
    public boolean isEvenement() {
        return TypeSeance.EVENEMENT.equals(this.type);
    }
}
