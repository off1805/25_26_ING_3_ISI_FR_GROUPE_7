package com.projetTransversalIsi.migration.application.dto;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;

public record EligibleClasseDTO(
        Long id,
        String code,
        String description
) {
    public static EligibleClasseDTO fromEntity(JpaClasseEntity entity) {
        return new EligibleClasseDTO(entity.getId(), entity.getCode(), entity.getDescription());
    }
}
