package com.projetTransversalIsi.emploi_temps.application.dto;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardList;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RetardListResponseDTO(
        Long id,
        Long classeId,
        LocalDate semaineDebut,
        LocalDateTime createdAt,
        boolean deleted
) {
    public static RetardListResponseDTO fromDomain(RetardList r) {
        return new RetardListResponseDTO(
                r.getId(), r.getClasseId(), r.getSemaineDebut(), r.getCreatedAt(), r.isDeleted()
        );
    }
}
