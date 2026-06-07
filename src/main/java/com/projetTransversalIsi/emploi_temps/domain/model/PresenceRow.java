package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    public void recalculatePresent(List<InfoPresenceRow> infoRows) {
        if (infoRows.isEmpty()) { this.present = null; return; }
        boolean allTrue  = infoRows.stream().allMatch(r -> Boolean.TRUE.equals(r.getIsPresent()));
        boolean allFalse = infoRows.stream().allMatch(r -> Boolean.FALSE.equals(r.getIsPresent()));
        if (allTrue)       this.present = Boolean.TRUE;
        else if (allFalse) this.present = Boolean.FALSE;
        else               this.present = null;
    }

    public void update(boolean present) {
        this.present = present;
    }
}
