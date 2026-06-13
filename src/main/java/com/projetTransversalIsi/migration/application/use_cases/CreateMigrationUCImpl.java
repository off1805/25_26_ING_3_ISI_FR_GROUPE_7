package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.CreateMigrationRequestDTO;
import com.projetTransversalIsi.migration.application.dto.MigrationResponseDTO;
import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentMigrationRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateMigrationUCImpl implements CreateMigrationUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataClasseRepository classeRepository;
    private final SpringDataStudentMigrationRepository migrationRepository;

    @Override
    @Transactional
    public MigrationResponseDTO execute(CreateMigrationRequestDTO command) {
        JpaUserEntity user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + command.userId()));

        if (user.getProfile() == null) {
            throw new IllegalStateException("L'utilisateur " + command.userId() + " n'a pas de profil étudiant.");
        }

        JpaStudentProfileEntity student = studentProfileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalStateException("Profil étudiant introuvable pour l'utilisateur " + command.userId()));

        JpaClasseEntity classeSource = student.getClasse();
        if (classeSource == null) {
            throw new IllegalStateException("L'étudiant " + command.userId() + " n'est inscrit dans aucune classe.");
        }

        JpaClasseEntity classeDestination = classeRepository.findById(command.classeDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Classe de destination introuvable : " + command.classeDestinationId()));

        if (classeDestination.getId().equals(classeSource.getId())) {
            throw new IllegalArgumentException("La classe de destination doit être différente de la classe actuelle.");
        }

        MigrationValidation.checkFiliereCompatibility(classeSource, classeDestination);

        migrationRepository.findByStudent_IdAndExecutedFalse(student.getId())
                .ifPresent(m -> {
                    throw new IllegalStateException("Une migration est déjà en attente pour cet étudiant.");
                });

        JpaStudentMigrationEntity migration = new JpaStudentMigrationEntity();
        migration.setStudent(student);
        migration.setClasseSource(classeSource);
        migration.setClasseDestination(classeDestination);
        migration.setExecuted(false);
        migration.setCreatedAt(LocalDateTime.now());

        return MigrationResponseDTO.fromEntity(migrationRepository.save(migration));
    }
}
