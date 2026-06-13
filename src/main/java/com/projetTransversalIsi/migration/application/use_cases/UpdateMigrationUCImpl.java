package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;
import com.projetTransversalIsi.migration.application.dto.UpdateMigrationRequestDTO;
import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentMigrationRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateMigrationUCImpl implements UpdateMigrationUC {

    private final SpringDataStudentMigrationRepository migrationRepository;
    private final SpringDataClasseRepository classeRepository;

    @Override
    @Transactional
    public MigrationResponseDTO execute(Long migrationId, UpdateMigrationRequestDTO command) {
        JpaStudentMigrationEntity migration = migrationRepository.findById(migrationId)
                .orElseThrow(() -> new IllegalArgumentException("Migration introuvable : " + migrationId));

        if (migration.isExecuted()) {
            throw new IllegalStateException("Cette migration a déjà été exécutée et ne peut plus être modifiée.");
        }

        JpaClasseEntity classeDestination = classeRepository.findById(command.classeDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Classe de destination introuvable : " + command.classeDestinationId()));

        if (classeDestination.getId().equals(migration.getClasseSource().getId())) {
            throw new IllegalArgumentException("La classe de destination doit être différente de la classe actuelle.");
        }

        MigrationValidation.checkFiliereCompatibility(migration.getClasseSource(), classeDestination);

        migration.setClasseDestination(classeDestination);
        return MigrationResponseDTO.fromEntity(migrationRepository.save(migration));
    }
}
