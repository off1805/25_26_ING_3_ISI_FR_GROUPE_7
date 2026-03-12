package com.projetTransversalIsi.cycle.application.dto;

import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleStatus;

public record CycleResponseDTO(
        Long id,
        String name,
        String code,
        int durationYears,
        String description,
        CycleStatus status
) {
    public static CycleResponseDTO fromDomain(Cycle cycle) {
        return new CycleResponseDTO(
                cycle.getId(),
                cycle.getName(),
                cycle.getCode(),
                cycle.getDurationYears(),
                cycle.getDescription(),
                cycle.getStatus()
        );
    }
}
