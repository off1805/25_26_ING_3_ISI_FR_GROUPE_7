package com.projetTransversalIsi.specialite.application.dto;

import com.projetTransversalIsi.specialite.domain.Specialite;

public record DeleteSpecialiteResponseDTO(
        Long id,
        String code,
        String libelle,
        String message
) {
    public static DeleteSpecialiteResponseDTO fromDomain(Specialite specialite) {
        return new DeleteSpecialiteResponseDTO(
                specialite.getId(),
                specialite.getCode(),
                specialite.getLibelle(),
                "Spécialité supprimée avec succès."
        );
    }
}
