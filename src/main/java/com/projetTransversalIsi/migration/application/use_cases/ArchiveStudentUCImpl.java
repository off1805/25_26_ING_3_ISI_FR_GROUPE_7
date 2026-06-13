package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentClasseHistoryEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArchiveStudentUCImpl implements ArchiveStudentUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataStudentClasseHistoryRepository historyRepository;
    private final SpringDataNiveauRepository niveauRepository;

    @Override
    @Transactional
    public void execute(Long userId) {
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

        JpaNiveauEntity niveauActuel = classe.getSpecialite().getNiveau();
        JpaFiliereEntity filiere = niveauActuel.getFiliere();

        if (filiere.isTroncCommun()) {
            throw new IllegalStateException("Un étudiant en tronc commun ne peut pas être archivé en phase terminale.");
        }

        List<JpaNiveauEntity> niveaux = niveauRepository.findByFiliereIdAndDeletedFalse(filiere.getId());
        int maxOrdre = niveaux.stream().mapToInt(JpaNiveauEntity::getOrdre).max()
                .orElseThrow(() -> new IllegalStateException("Aucun niveau trouvé pour la filière " + filiere.getId()));

        if (niveauActuel.getOrdre() != maxOrdre) {
            throw new IllegalStateException("L'étudiant " + userId + " n'est pas en phase terminale de sa filière.");
        }

        Optional<JpaStudentClasseHistoryEntity> openHistory =
                historyRepository.findByStudentIdAndDateFinIsNull(student.getId());
        openHistory.ifPresent(h -> {
            h.setDateFin(LocalDate.now());
            historyRepository.save(h);
        });

        student.setClasse(null);
        studentProfileRepository.save(student);

        user.setStatus(UserStatus.GRADUATED);
        userRepository.save(user);
    }
}
