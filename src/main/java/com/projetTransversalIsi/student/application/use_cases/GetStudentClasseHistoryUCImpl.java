package com.projetTransversalIsi.student.application.use_cases;

import com.projetTransversalIsi.student.application.dto.ClasseHistoriqueResponseDTO;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSpecialiteEntity;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentClasseHistoryEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetStudentClasseHistoryUCImpl implements GetStudentClasseHistoryUC {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataStudentClasseHistoryRepository historyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClasseHistoriqueResponseDTO> execute(Long userId) {
        JpaUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));

        if (user.getProfile() == null) {
            throw new IllegalStateException("L'utilisateur " + userId + " n'a pas de profil étudiant.");
        }

        studentProfileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalStateException("Profil étudiant introuvable pour l'utilisateur " + userId));

        List<JpaStudentClasseHistoryEntity> history =
                historyRepository.findByStudentIdOrderByDateDebutDesc(user.getProfile().getId());

        return history.stream().map(h -> {
            JpaClasseEntity classe = h.getClasse();
            JpaSpecialiteEntity specialite = classe.getSpecialite();
            return new ClasseHistoriqueResponseDTO(
                    classe.getId(),
                    classe.getCode(),
                    classe.getDescription(),
                    specialite != null ? specialite.getId() : null,
                    specialite != null ? specialite.getLibelle() : null,
                    h.getDateDebut(),
                    h.getDateFin(),
                    h.getDateFin() == null
            );
        }).toList();
    }
}
