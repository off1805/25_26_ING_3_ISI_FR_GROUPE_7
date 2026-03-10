package com.projetTransversalIsi.specialite.application.dto;

import com.projetTransversalIsi.specialite.domain.Specialite;

public record SpecialiteResponseDTO(
        Long id,
        String code,
        String libelle,
        String description,
        String brancheCode,
        int niveauMinimum,
        boolean active
) {
    public static SpecialiteResponseDTO fromDomain(Specialite specialite) {
        return new SpecialiteResponseDTO(
                specialite.getId(),
                specialite.getCode(),
                specialite.getLibelle(),
                specialite.getDescription(),
                specialite.getBrancheCode(),
                specialite.getNiveauMinimum(),
                specialite.isActive()
        );
    }
}
