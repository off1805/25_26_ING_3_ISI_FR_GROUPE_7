package com.projetTransversalIsi.migration.application.dto;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentExpulsionEntity;

import java.time.LocalDateTime;

public record ExpulsionResponseDTO(
        Long id,
        Long userId,
        String motif,
        String justificatifUrl,
        String justificatifNomOriginal,
        LocalDateTime expelledAt
) {
    public static ExpulsionResponseDTO fromEntity(JpaStudentExpulsionEntity entity) {
        var student = entity.getStudent();
        return new ExpulsionResponseDTO(
                entity.getId(),
                student.getUser() != null ? student.getUser().getId() : null,
                entity.getMotif(),
                entity.getJustificatifUrl(),
                entity.getJustificatifNomOriginal(),
                entity.getExpelledAt()
        );
    }
}
