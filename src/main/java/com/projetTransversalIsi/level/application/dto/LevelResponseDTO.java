package com.projetTransversalIsi.level.application.dto;

import com.projetTransversalIsi.level.domain.level;

public record LevelResponseDTO(Long id, String nom, String description) {
    public static LevelResponseDTO fromDomain(level level) {
        return new LevelResponseDTO(
            level.getId(),
            level.getNom(),
            level.getDescription()
        );
    }
}
