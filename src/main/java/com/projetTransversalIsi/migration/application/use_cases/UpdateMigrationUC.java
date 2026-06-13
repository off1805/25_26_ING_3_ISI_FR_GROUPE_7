package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;
import com.projetTransversalIsi.migration.application.dto.UpdateMigrationRequestDTO;

public interface UpdateMigrationUC {
    MigrationResponseDTO execute(Long migrationId, UpdateMigrationRequestDTO command);
}
