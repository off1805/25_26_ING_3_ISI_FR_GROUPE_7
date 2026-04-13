package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Seance> seances = new HashSet<>();
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    private EmploiStatus status;

    public EmploiTemps(LocalDate dateDebut, LocalDate dateFin,
                       Integer semaine, Long classeId) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.semaine = semaine;
        this.classeId = classeId;
        this.deleted = false;
    }

    // Méthodes métier
    public void update(LocalDate dateDebut, LocalDate dateFin,
                       Integer semaine, Long classeId) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.semaine = semaine;
        this.classeId = classeId;
    }

    public void addSeance(Seance seance) {
        // Vérifier que la séance est dans la période de l'emploi du temps
        if (seance.getDateSeance().isBefore(dateDebut) ||
                seance.getDateSeance().isAfter(dateFin)) {
            throw new IllegalArgumentException(
                    "La séance est en dehors de la période de l'emploi du temps"
            );
        }

        // Vérifier qu'elle n'est pas déjà ajoutée
        if (!seances.contains(seance)) {
            seances.add(seance);
        }
    }

    public void removeSeance(Seance seance) {
        seances.remove(seance);
    }

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
