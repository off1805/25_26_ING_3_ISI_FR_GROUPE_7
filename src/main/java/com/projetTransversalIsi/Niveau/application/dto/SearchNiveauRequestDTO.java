package com.projetTransversalIsi.Niveau.application.dto;

public record SearchNiveauRequestDTO(
        Integer ordre,
        String description,
        boolean includeDeleted
) {
}
