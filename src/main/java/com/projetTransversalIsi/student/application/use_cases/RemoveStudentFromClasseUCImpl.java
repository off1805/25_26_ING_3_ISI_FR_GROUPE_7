package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentClasseHistoryEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RemoveStudentFromClasseUCImpl implements RemoveStudentFromClasseUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataStudentClasseHistoryRepository historyRepository;

    @Override
    @Transactional
    public void execute(Long userId, Long classeId) {
        JpaUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));

        if (user.getProfile() == null) {
            throw new IllegalStateException("L'utilisateur " + userId + " n'a pas de profil étudiant.");
        }

        JpaStudentProfileEntity profile = studentProfileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalStateException("Profil étudiant introuvable pour l'utilisateur " + userId));

        if (profile.getClasse() == null || !profile.getClasse().getId().equals(classeId)) {
            throw new IllegalStateException("L'étudiant " + userId + " n'est pas dans la classe " + classeId);
        }

        Optional<JpaStudentClasseHistoryEntity> openHistory = historyRepository.findByStudentIdAndDateFinIsNull(profile.getId());
        openHistory.ifPresent(h -> {
            h.setDateFin(LocalDate.now());
            historyRepository.save(h);
        });

        profile.setClasse(null);
        studentProfileRepository.save(profile);
    }
}
