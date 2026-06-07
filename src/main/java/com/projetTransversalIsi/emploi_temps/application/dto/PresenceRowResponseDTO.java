package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;

public record PresenceRowResponseDTO(
        Long id,
        Long presenceListId,
        Long etudiantId,
        Boolean present,
        boolean retard
) {
    public static PresenceRowResponseDTO fromDomain(PresenceRow r) {
        return new PresenceRowResponseDTO(
                r.getId(), r.getPresenceListId(), r.getEtudiantId(), r.getPresent(), r.isRetard()
        );
    }
}
