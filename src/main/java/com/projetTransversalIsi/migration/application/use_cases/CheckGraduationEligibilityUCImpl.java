package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.GraduationEligibilityDTO;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckGraduationEligibilityUCImpl implements CheckGraduationEligibilityUC {

    private final SpringDataClasseRepository classeRepository;
    private final SpringDataNiveauRepository niveauRepository;

    @Override
    @Transactional
    public GraduationEligibilityDTO execute(Long classeId) {
        JpaClasseEntity classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new IllegalArgumentException("Classe introuvable : " + classeId));

        JpaNiveauEntity niveauActuel = classe.getSpecialite().getNiveau();
        JpaFiliereEntity filiere = niveauActuel.getFiliere();

        if (filiere.isTroncCommun()) {
            return new GraduationEligibilityDTO(false);
        }

        List<JpaNiveauEntity> niveaux = niveauRepository.findByFiliereIdAndDeletedFalse(filiere.getId());
        int maxOrdre = niveaux.stream().mapToInt(JpaNiveauEntity::getOrdre).max().orElse(Integer.MIN_VALUE);

        return new GraduationEligibilityDTO(niveauActuel.getOrdre() == maxOrdre);
    }
}
