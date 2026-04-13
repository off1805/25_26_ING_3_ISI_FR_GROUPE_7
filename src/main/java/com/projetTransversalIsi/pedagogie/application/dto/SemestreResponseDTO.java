package com.projetTransversalIsi.pedagogie.application.dto;

public record SemestreResponseDTO(
        Long id,
        Integer numero,
        String libelle,
        Long anneeScolaireId,
        Long specialiteId
) {
}