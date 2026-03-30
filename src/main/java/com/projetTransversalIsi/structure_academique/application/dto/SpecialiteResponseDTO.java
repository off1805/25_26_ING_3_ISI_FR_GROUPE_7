package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;

public record SpecialiteResponseDTO(
        Long id,
        String code,
        String libelle,
        String description,
        Long niveauId,
        Integer niveauOrdre,
        String niveauDescription,
        boolean active
) {
    public static SpecialiteResponseDTO fromDomain(Specialite specialite) {
        return new SpecialiteResponseDTO(
                specialite.getId(),
                specialite.getCode(),
                specialite.getLibelle(),
                specialite.getDescription(),
                specialite.getNiveau() != null ? specialite.getNiveau().getId() : null,
                specialite.getNiveau() != null ? specialite.getNiveau().getOrdre() : null,
                specialite.getNiveau() != null ? specialite.getNiveau().getDescription() : null,
                specialite.isActive()
        );
    }
}
