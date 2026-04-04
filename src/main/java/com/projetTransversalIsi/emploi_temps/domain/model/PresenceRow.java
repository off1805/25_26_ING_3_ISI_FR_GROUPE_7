package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresenceRow {

    private Long id;
    private Long presenceListId;
    private Long etudiantId;
    private boolean present;
    private float heuresAbsence;

    public PresenceRow(Long presenceListId, Long etudiantId, boolean present) {
        this.presenceListId = presenceListId;
        this.etudiantId = etudiantId;
        this.present = present;
        this.heuresAbsence = 0f;
    }

    public void update(boolean present, float heuresAbsence) {
        this.present = present;
        this.heuresAbsence = heuresAbsence;
    }
}
