package com.projetTransversalIsi.structure_academique.application.dto;

import java.time.LocalDateTime;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;

public record DeleteCycleResponseDTO(Long id, String name, String code, LocalDateTime deletedAt) {
    public static DeleteCycleResponseDTO fromDomain(Cycle cycle) {
        return new DeleteCycleResponseDTO(
                cycle.getId(),
                cycle.getName(),
                cycle.getCode(),
                cycle.getDeletedAt()
        );
    }
}
