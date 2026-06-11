package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Une ligne par étudiant dans une feuille de retard hebdomadaire (RetardList).
// Le détail jour par jour (6 colonnes) est porté par les InfoRetardRow associées.
@Getter
@Setter
@NoArgsConstructor
public class RetardRow {

    private Long id;
    private Long retardListId;
    private Long etudiantId;

    public RetardRow(Long retardListId, Long etudiantId) {
        this.retardListId = retardListId;
        this.etudiantId = etudiantId;
    }
}
