package com.projetTransversalIsi.structure_academique.application.dto;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.model.CycleStatus;

public record CycleResponseDTO(
        Long id,
        Long schoolId,
        String name,
        String code,
        int durationYears,
        String description,
        CycleStatus status
) {
    public static CycleResponseDTO fromDomain(Cycle cycle) {
        return new CycleResponseDTO(
                cycle.getId(),
                cycle.getSchoolId(),
                cycle.getName(),
                cycle.getCode(),
                cycle.getDurationYears(),
                cycle.getDescription(),
                cycle.getStatus()
        );
    }
}
