package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.ExpulsionResponseDTO;
import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentExpulsionEntity;
import com.projetTransversalIsi.migration.infrastructure.repository.SpringDataStudentExpulsionRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentClasseHistoryEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpelStudentUCImpl implements ExpelStudentUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataStudentClasseHistoryRepository historyRepository;
    private final SpringDataStudentExpulsionRepository expulsionRepository;

    @Value("${app.upload.dir.expulsions:uploads/expulsions}")
    private String uploadDir;

    @Override
    @Transactional
    public ExpulsionResponseDTO execute(Long userId, String motif, MultipartFile justificatif) {
        JpaUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));

        if (user.getProfile() == null) {
            throw new IllegalStateException("L'utilisateur " + userId + " n'a pas de profil étudiant.");
        }

        JpaStudentProfileEntity student = studentProfileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalStateException("Profil étudiant introuvable pour l'utilisateur " + userId));

        JpaClasseEntity classe = student.getClasse();
        if (classe == null) {
            throw new IllegalStateException("L'étudiant " + userId + " n'est inscrit dans aucune classe.");
        }

        if (justificatif == null || justificatif.isEmpty()) {
            throw new IllegalArgumentException("Un justificatif est requis pour renvoyer un étudiant.");
        }

        String ext = getExtension(justificatif.getOriginalFilename());
        String filename = student.getId() + "_" + UUID.randomUUID() + ext;
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Files.copy(justificatif.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du justificatif", e);
        }

        Optional<JpaStudentClasseHistoryEntity> openHistory =
                historyRepository.findByStudentIdAndDateFinIsNull(student.getId());
        openHistory.ifPresent(h -> {
            h.setDateFin(LocalDate.now());
            historyRepository.save(h);
        });

        JpaStudentExpulsionEntity expulsion = new JpaStudentExpulsionEntity();
        expulsion.setStudent(student);
        expulsion.setClasse(classe);
        expulsion.setMotif(motif);
        expulsion.setJustificatifUrl("/uploads/expulsions/" + filename);
        expulsion.setJustificatifNomOriginal(justificatif.getOriginalFilename());
        expulsion.setExpelledAt(LocalDateTime.now());
        JpaStudentExpulsionEntity saved = expulsionRepository.save(expulsion);

        student.setClasse(null);
        studentProfileRepository.save(student);

        user.setStatus(UserStatus.EXPELLED);
        userRepository.save(user);

        return ExpulsionResponseDTO.fromEntity(saved);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".pdf";
        return filename.substring(filename.lastIndexOf("."));
    }
}
