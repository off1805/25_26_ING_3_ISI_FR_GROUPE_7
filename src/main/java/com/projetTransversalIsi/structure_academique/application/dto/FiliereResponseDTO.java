package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import java.time.LocalDateTime;

public record FiliereResponseDTO(
        Long id,
        String code,
        String nom,
        String description,
        boolean deleted,
        LocalDateTime deletedAt,
        Long cycleId,
        String cycleName
) {
    public static FiliereResponseDTO fromDomain(Filiere filiere) {
        return new FiliereResponseDTO(
                filiere.getId(),
                filiere.getCode(),
                filiere.getNom(),
                filiere.getDescription(),
                filiere.isDeleted(),
                filiere.getDeletedAt(),
                filiere.getCycle() != null ? filiere.getCycle().getId() : null,
                filiere.getCycle() != null ? filiere.getCycle().getName() : null
        );
    }
}
