package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;

import java.util.List;

public interface ListPendingMigrationsUC {
    List<MigrationResponseDTO> execute(Long filiereId);
}
