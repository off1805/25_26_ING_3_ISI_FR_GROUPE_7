package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.migration.application.dto.EligibleClasseDTO;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetEligibleDestinationClassesUCImpl implements GetEligibleDestinationClassesUC {

    private final SpringDataClasseRepository classeRepository;

    @Override
    @Transactional
    public List<EligibleClasseDTO> execute(Long classeSourceId) {
        JpaClasseEntity classeSource = classeRepository.findById(classeSourceId)
                .orElseThrow(() -> new IllegalArgumentException("Classe introuvable : " + classeSourceId));

        JpaFiliereEntity filiereSource = classeSource.getSpecialite().getNiveau().getFiliere();
        int ordreDestination = classeSource.getSpecialite().getNiveau().getOrdre() + 1;

        List<JpaClasseEntity> candidates = filiereSource.isTroncCommun()
                ? classeRepository.findBySpecialite_Niveau_Filiere_Cycle_IdAndSpecialite_Niveau_Ordre(filiereSource.getCycle().getId(), ordreDestination)
                : classeRepository.findBySpecialite_Niveau_Filiere_IdAndSpecialite_Niveau_Ordre(filiereSource.getId(), ordreDestination);

        return candidates.stream()
                .filter(c -> !c.getId().equals(classeSourceId))
                .map(EligibleClasseDTO::fromEntity)
                .toList();
    }
}
