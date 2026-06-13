package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentMigrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListPendingMigrationsUCImpl implements ListPendingMigrationsUC {

    private final SpringDataStudentMigrationRepository migrationRepository;

    @Override
    @Transactional
    public List<MigrationResponseDTO> execute(Long filiereId) {
        return migrationRepository.findByExecutedFalseAndClasseSource_Specialite_Niveau_Filiere_Id(filiereId)
                .stream()
                .map(MigrationResponseDTO::fromEntity)
                .toList();
    }
}
