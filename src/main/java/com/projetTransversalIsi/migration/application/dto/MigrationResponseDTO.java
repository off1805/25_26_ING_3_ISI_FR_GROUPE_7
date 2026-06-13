package com.projetTransversalIsi.migration.application.dto;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;

import java.time.LocalDateTime;

public record MigrationResponseDTO(
        Long id,
        Long userId,
        String matricule,
        String nom,
        String prenom,
        Long classeSourceId,
        String classeSourceCode,
        Long classeDestinationId,
        String classeDestinationCode,
        boolean executed,
        LocalDateTime createdAt
) {
    public static MigrationResponseDTO fromEntity(JpaStudentMigrationEntity entity) {
        var student = entity.getStudent();
        return new MigrationResponseDTO(
                entity.getId(),
                student.getUser() != null ? student.getUser().getId() : null,
                student.getMatricule(),
                student.getNom(),
                student.getPrenom(),
                entity.getClasseSource().getId(),
                entity.getClasseSource().getCode(),
                entity.getClasseDestination().getId(),
                entity.getClasseDestination().getCode(),
                entity.isExecuted(),
                entity.getCreatedAt()
        );
    }
}
