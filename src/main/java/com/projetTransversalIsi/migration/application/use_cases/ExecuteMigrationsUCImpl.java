package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentMigrationRepository;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataAnneeScolaireRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentClasseHistoryEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExecuteMigrationsUCImpl implements ExecuteMigrationsUC {

    private final SpringDataStudentMigrationRepository migrationRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataStudentClasseHistoryRepository historyRepository;
    private final SpringDataAnneeScolaireRepository anneeScolaireRepository;

    @Override
    @Transactional
    public void execute() {
        List<JpaStudentMigrationEntity> pending = migrationRepository.findByExecutedFalse();
        if (pending.isEmpty()) {
            return;
        }

        JpaAnneeScolaireEntity anneeScolaire = anneeScolaireRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Aucune année scolaire active."));

        for (JpaStudentMigrationEntity migration : pending) {
            JpaStudentProfileEntity student = migration.getStudent();

            Optional<JpaStudentClasseHistoryEntity> openHistory =
                    historyRepository.findByStudentIdAndDateFinIsNull(student.getId());
            openHistory.ifPresent(h -> {
                h.setDateFin(LocalDate.now());
                historyRepository.save(h);
            });

            student.setClasse(migration.getClasseDestination());
            studentProfileRepository.save(student);

            JpaStudentClasseHistoryEntity newHistory = new JpaStudentClasseHistoryEntity();
            newHistory.setStudent(student);
            newHistory.setClasse(migration.getClasseDestination());
            newHistory.setAnneeScolaire(anneeScolaire);
            newHistory.setDateDebut(LocalDate.now());
            historyRepository.save(newHistory);

            migration.setExecuted(true);
            migration.setExecutedAt(LocalDateTime.now());
            migrationRepository.save(migration);
        }
    }
}
