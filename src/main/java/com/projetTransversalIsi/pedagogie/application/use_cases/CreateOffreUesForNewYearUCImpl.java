package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUesForNewYearResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.infrastructure.UeMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.EnseignantClasseLink;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaUeEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataOffreUeRepository;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataUeRepository;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.UeSpec;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreateOffreUesForNewYearUCImpl implements CreateOffreUesForNewYearUC {

    private final SpringDataUeRepository springDataUeRepository;
    private final UeMapper ueMapper;
    private final OffreUeRepository offreUeRepository;
    private final CreateOffreUeUC createOffreUeUC;
    private final SpringDataOffreUeRepository springDataOffreUeRepository;

    @Override
    @Transactional
    public CreateOffreUesForNewYearResponseDTO execute(Long newAnneeScolaireId, Long previousAnneeScolaireId) {
        List<JpaUeEntity> ues = springDataUeRepository.findAll(UeSpec.isDeleted(false));

        int offresCreees = 0;
        int offresExistantes = 0;
        int affectationsReprises = 0;

        for (JpaUeEntity ueEntity : ues) {
            Ue ue = ueMapper.jpaUeEntityToUe(ueEntity);

            if (offreUeRepository.offreUeAlreadyExists(ue.getId(), newAnneeScolaireId)) {
                offresExistantes++;
                continue;
            }

            OffreUe created = createOffreUeUC.execute(new CreateOffreUeRequestDTO(ue.getId(), newAnneeScolaireId));
            offresCreees++;

            if (previousAnneeScolaireId != null) {
                affectationsReprises += reprendreAffectations(created, ue, previousAnneeScolaireId);
            }
        }

        return new CreateOffreUesForNewYearResponseDTO(offresCreees, offresExistantes, affectationsReprises);
    }

    private int reprendreAffectations(OffreUe newOffre, Ue ue, Long previousAnneeScolaireId) {
        Optional<JpaOffreUeEntity> previousOffre = springDataOffreUeRepository
                .findByUe_IdAndAnneeScolaire_Id(ue.getId(), previousAnneeScolaireId);

        if (previousOffre.isEmpty() || previousOffre.get().getEnseignantAssignments().isEmpty()) {
            return 0;
        }

        Set<EnseignantClasseLink> filtered = previousOffre.get().getEnseignantAssignments().stream()
                .filter(link -> ue.getEnseignantIds().contains(link.getEnseignantId()))
                .collect(Collectors.toCollection(HashSet::new));

        if (filtered.isEmpty()) {
            return 0;
        }

        JpaOffreUeEntity newEntity = springDataOffreUeRepository.findById(newOffre.getId())
                .orElseThrow(() -> new RuntimeException("OffreUe introuvable : " + newOffre.getId()));
        newEntity.setEnseignantAssignments(filtered);
        springDataOffreUeRepository.save(newEntity);

        return filtered.size();
    }
}
