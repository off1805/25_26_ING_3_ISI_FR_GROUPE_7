package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardRow;

public record RetardRowResponseDTO(
        Long id,
        Long retardListId,
        Long etudiantId
) {
    public static RetardRowResponseDTO fromDomain(RetardRow r) {
        return new RetardRowResponseDTO(r.getId(), r.getRetardListId(), r.getEtudiantId());
    }
}
