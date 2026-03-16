package com.projetTransversalIsi.emploi_Temps.domain;


import com.projetTransversalIsi.seance.domain.Seance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class EmploiTemps {

    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer semaine;
    private Long classeId;
    private Set<Seance> seances = new HashSet<>();
    private boolean deleted = false;
    private LocalDateTime deletedAt;

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
