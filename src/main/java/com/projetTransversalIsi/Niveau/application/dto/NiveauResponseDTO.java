package com.projetTransversalIsi.Niveau.application.dto;

import com.projetTransversalIsi.Niveau.domain.Niveau;
import java.time.LocalDateTime;

public record NiveauResponseDTO(
        Long id,
        int ordre,
        String description,
        boolean deleted,
        LocalDateTime deletedAt,
        Long filiereId,
        String filiereNom
) {
    public static NiveauResponseDTO fromDomain(Niveau niveau) {
        return new NiveauResponseDTO(
                niveau.getId(),
                niveau.getOrdre(),
                niveau.getDescription(),
                niveau.isDeleted(),
                niveau.getDeletedAt(),
                niveau.getFiliere() != null ? niveau.getFiliere().getId() : null,
                niveau.getFiliere() != null ? niveau.getFiliere().getNom() : null
        );
    }
}
