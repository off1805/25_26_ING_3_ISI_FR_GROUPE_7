package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.CreateMigrationRequestDTO;
import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;

public interface CreateMigrationUC {
    MigrationResponseDTO execute(CreateMigrationRequestDTO command);
}
