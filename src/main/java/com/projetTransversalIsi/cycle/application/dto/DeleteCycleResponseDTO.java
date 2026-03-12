package com.projetTransversalIsi.cycle.application.dto;

import com.projetTransversalIsi.cycle.domain.Cycle;

import java.time.LocalDateTime;

public record DeleteCycleResponseDTO(Long id, String name, String code, LocalDateTime deletedAt) {
    public static DeleteCycleResponseDTO fromDomain(Cycle cycle) {
        return new DeleteCycleResponseDTO(
                cycle.getId(),
                cycle.getName(),
                cycle.getCode(),
               // cycle.isDeleted(),
                cycle.getDeletedAt()
        );
    }
}
