package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Agrégat racine du module emploi de temps.
// classeId, seances : relations vers d'autres agrégats référencées par ID (pas par objet JPA)
// pour respecter les frontières d'agrégats de l'architecture hexagonale.
// Le @Builder + constructeurs privés imposent que toute création passe par EmploiTempsService,
// qui vérifie l'absence de chevauchement de période avant d'instancier.
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class EmploiTemps {
    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer semaine;
    private Long classeId;
    private Long anneeScolaireId;
    // Set pour éviter les doublons de séances sans logique d'ordre ; hashCode/equals sur id JPA.
    private Set<Seance> seances = new HashSet<>();
    private boolean deleted = false;
    // deletedAt sert d'horodatage d'audit ; null tant que l'entité est active.
    private LocalDateTime deletedAt;
    // Calculé côté lecture (PAST/ONGOING/UPCOMING) via comparaison avec LocalDate.now().
    private EmploiStatus status;

    // Constructeur métier utilisé par EmploiTempsService.createEmploiTempsWithSeances
    // (sans @Builder car la création directe via le constructeur est plus lisible dans ce cas).
    public EmploiTemps(LocalDate dateDebut, LocalDate dateFin,
                       Integer semaine, Long classeId) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.semaine = semaine;
        this.classeId = classeId;
        this.deleted = false;
    }

    public void update(LocalDate dateDebut, LocalDate dateFin,
                       Integer semaine, Long classeId) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.semaine = semaine;
        this.classeId = classeId;
    }

    public void addSeance(Seance seance) {
        // Invariant métier : une séance doit tomber dans la fenêtre temporelle de son emploi.
        // Cette vérification côté domaine double-protège contre un bug dans le service appelant.
        System.out.println("Date debut planning: "+dateDebut);
        System.out.println("Date fin planning: "+dateFin);
        System.out.println("Date seance: "+seance.getDateSeance());

        if (seance.getDateSeance().isBefore(dateDebut) ||
                seance.getDateSeance().isAfter(dateFin)) {
            throw new IllegalArgumentException(
                    "La séance est en dehors de la période de l'emploi du temps"
            );
        }

        // Idempotence : ajouter deux fois la même séance ne crée pas de doublon.
        if (!seances.contains(seance)) {
            seances.add(seance);
        }
    }

    public void removeSeance(Seance seance) {
        seances.remove(seance);
    }

    // Soft-delete : on ne supprime jamais physiquement pour garder l'historique d'audit.
    // La garde against double-delete évite d'écraser deletedAt avec une valeur plus récente.
    public void delete() {
        if (this.deleted) {
            throw new IllegalStateException("L'emploi du temps " + this.id + " est déjà supprimé");
        }
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        if (!this.deleted) {
            throw new IllegalStateException("L'emploi du temps " + this.id + " n'est pas supprimé");
        }
        this.deleted = false;
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
