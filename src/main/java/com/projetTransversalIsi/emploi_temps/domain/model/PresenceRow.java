package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Ligne de presence individuelle : un etudiant dans une feuille de presence.
@Getter
@Setter
@NoArgsConstructor
public class PresenceRow {

    private Long id;
    private Long presenceListId;
    private Long etudiantId;
    private Boolean present;
    /** true = etudiant arrive en retard (present mais tardif). */
    private boolean retard = false;

    public PresenceRow(Long presenceListId, Long etudiantId, Boolean present) {
        this.presenceListId = presenceListId;
        this.etudiantId = etudiantId;
        this.present = present;
    }

    public PresenceRow(Long presenceListId, Long etudiantId, Boolean present, boolean retard) {
        this.presenceListId = presenceListId;
        this.etudiantId = etudiantId;
        this.present = present;
        this.retard = retard;
    }

    // Recalcule present depuis l ensemble des InfoPresenceRow liees a cette ligne.
    public void recalculatePresent(List<InfoPresenceRow> infoRows) {
        if (infoRows.isEmpty()) { this.present = null; return; }
        boolean allTrue  = infoRows.stream().allMatch(r -> Boolean.TRUE.equals(r.getIsPresent()));
        boolean allFalse = infoRows.stream().allMatch(r -> Boolean.FALSE.equals(r.getIsPresent()));
        if (allTrue)       this.present = Boolean.TRUE;
        else if (allFalse) this.present = Boolean.FALSE;
        else               this.present = null;
    }

    // Correction directe -- court-circuite le calcul automatique.
    public void update(boolean present) {
        this.present = present;
    }

    // Marque le retard (le surveillant peut le faire apres l appel).
    public void markRetard(boolean retard) {
        this.retard = retard;
        if (retard) this.present = Boolean.TRUE;
    }
}
