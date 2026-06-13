package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentMigrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelMigrationUCImpl implements CancelMigrationUC {

    private final SpringDataStudentMigrationRepository migrationRepository;

    @Override
    @Transactional
    public void execute(Long migrationId) {
        JpaStudentMigrationEntity migration = migrationRepository.findById(migrationId)
                .orElseThrow(() -> new IllegalArgumentException("Migration introuvable : " + migrationId));

        if (migration.isExecuted()) {
            throw new IllegalStateException("Cette migration a déjà été exécutée et ne peut plus être annulée.");
        }

        migrationRepository.delete(migration);
    }
}
