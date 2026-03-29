package com.projetTransversalIsi.Filiere.application.dto;

import lombok.Getter;


public record SearchFiliereRequestDTO(
        String code,
        String nom,
        Long cycleId,
        Boolean includeDeleted
) {
}
