package com.projetTransversalIsi.Filiere.application.dto;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import java.time.LocalDateTime;

public record FiliereResponseDTO(
        Long id,
        String code,
        String nom,
        String description,
        boolean deleted,
        LocalDateTime deletedAt
) {
    public static FiliereResponseDTO fromDomain(Filiere filiere) {
        return new FiliereResponseDTO(
                filiere.getId(),
                filiere.getCode(),
                filiere.getNom(),
                filiere.getDescription(),
                filiere.isDeleted(),
                filiere.getDeletedAt()
        );
    }
}
