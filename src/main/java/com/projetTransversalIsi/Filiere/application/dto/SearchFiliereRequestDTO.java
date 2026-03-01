package com.projetTransversalIsi.Filiere.application.dto;

public record SearchFiliereRequestDTO(
        String code,
        String nom,
        Boolean includeDeleted
) {
}
