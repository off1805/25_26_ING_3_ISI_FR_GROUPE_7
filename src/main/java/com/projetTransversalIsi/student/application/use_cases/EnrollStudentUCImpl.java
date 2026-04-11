package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.security.infrastructure.JpaRoleEntity;
import com.projetTransversalIsi.security.services.PasswordHasherAC;
import com.projetTransversalIsi.student.application.dto.EnrollStudentRequestDTO;
import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;
import com.projetTransversalIsi.student.domain.exceptions.StudentAlreadyEnrolledException;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EnrollStudentUCImpl implements EnrollStudentUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataClasseRepository classeRepository;
    private final PasswordHasherAC passwordHasher;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public EnrollStudentResponseDTO execute(EnrollStudentRequestDTO command) {

        JpaClasseEntity classe = classeRepository.findById(command.classeId())
                .orElseThrow(() -> new IllegalArgumentException("Classe introuvable : " + command.classeId()));

        Optional<JpaUserEntity> existingUser = userRepository.findByEmail(command.email());

        if (existingUser.isPresent()) {
            // L'utilisateur existe — vérifier si son profil étudiant est déjà dans une classe
            JpaUserEntity user = existingUser.get();

            if (user.getProfile() == null) {
                throw new IllegalStateException("L'utilisateur " + command.email() + " n'a pas de profil.");
            }

            // Récupérer le student profile
            JpaStudentProfileEntity studentProfile = studentProfileRepository.findById(user.getProfile().getId())
                    .orElseThrow(() -> new IllegalStateException("Profil étudiant introuvable pour " + command.email()));

            if (studentProfile.getClasse() != null) {
                throw new StudentAlreadyEnrolledException(command.email());
            }

            // Affecter la classe
            studentProfile.setClasse(classe);
            studentProfileRepository.save(studentProfile);

            return  EnrollStudentResponseDTO.builder()
                    .userId(user.getId())
                    .nom(studentProfile.getNom())
                    .prenom(studentProfile.getPrenom())
                    .matricule(studentProfile.getMatricule())
                    .classeId(studentProfile.getClasse().getId())
                    .build();


        } else {
            // L'utilisateur n'existe pas — créer profil + user
            JpaStudentProfileEntity studentProfile = new JpaStudentProfileEntity();
            studentProfile.setNom(command.nom());
            studentProfile.setPrenom(command.prenom());
            studentProfile.setMatricule(command.matricule());
            studentProfile.setNumeroTelephone(command.numeroTelephone());
            studentProfile.setClasse(classe);
            JpaStudentProfileEntity savedProfile = studentProfileRepository.save(studentProfile);

            JpaRoleEntity role = entityManager.getReference(JpaRoleEntity.class, "STUDENT");

            JpaUserEntity newUser = new JpaUserEntity();
            newUser.setEmail(command.email());
            newUser.setPassword(passwordHasher.encode("password"));
            newUser.setRole(role);
            newUser.setProfile(savedProfile);
            newUser.setPermissions(Set.of());
            JpaUserEntity savedUser = userRepository.save(newUser);

            return  EnrollStudentResponseDTO.builder()
                    .userId(savedUser.getId())
                    .nom(studentProfile.getNom())
                    .prenom(studentProfile.getPrenom())
                    .matricule(studentProfile.getMatricule())
                    .classeId(studentProfile.getClasse().getId())
                    .build();
        }
    }
}
