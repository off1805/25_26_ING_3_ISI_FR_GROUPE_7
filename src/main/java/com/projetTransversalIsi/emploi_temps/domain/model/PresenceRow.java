package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Ligne de présence individuelle : un étudiant dans une feuille de présence.
// La valeur de `present` est calculée depuis les InfoPresenceRow de tous les appels liés :
//   true  → l'étudiant est présent sur TOUS les appels de la feuille (toutes isPresent=true)
//   false → l'étudiant est absent sur TOUS les appels (toutes isPresent=false)
//   null  → statut non entièrement résolu : certains appels sont en cours (isPresent=null),
//            ou les résultats sont mixtes (présent sur un appel, absent sur un autre).
//            Cet état est transitoire ; il disparaît quand tous les appels sont clôturés.
@Getter
@Setter
@NoArgsConstructor
public class PresenceRow {

    private Long id;
    private Long presenceListId;
    private Long etudiantId;
    private Boolean present;

    public PresenceRow(Long presenceListId, Long etudiantId, Boolean present) {
        this.presenceListId = presenceListId;
        this.etudiantId = etudiantId;
        this.present = present;
    }

    // Recalcule `present` depuis l'ensemble des InfoPresenceRow liées à cette ligne.
    public void recalculatePresent(List<InfoPresenceRow> infoRows) {
        if (infoRows.isEmpty()) { this.present = null; return; }
        boolean allTrue  = infoRows.stream().allMatch(r -> Boolean.TRUE.equals(r.getIsPresent()));
        boolean allFalse = infoRows.stream().allMatch(r -> Boolean.FALSE.equals(r.getIsPresent()));
        if (allTrue)       this.present = Boolean.TRUE;
        else if (allFalse) this.present = Boolean.FALSE;
        else               this.present = null;
    }

    // Correction directe par l'enseignant — court-circuite le calcul automatique.
    public void update(boolean present) {
        this.present = present;
    }
}
