package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Feuille de retard hebdomadaire d'une classe : un seul RetardList par (classeId, semaineDebut).
// semaineDebut est le lundi de la semaine concernée.
@Getter
@Setter
@NoArgsConstructor
public class RetardList {

    private Long id;
    private Long classeId;
    private Long anneeScolaireId;
    private LocalDate semaineDebut;
    private LocalDateTime createdAt;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public RetardList(Long classeId, LocalDate semaineDebut) {
        this.classeId = classeId;
        this.semaineDebut = semaineDebut;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    public void delete() {
        if (this.deleted) throw new IllegalStateException("Liste déjà supprimée");
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
