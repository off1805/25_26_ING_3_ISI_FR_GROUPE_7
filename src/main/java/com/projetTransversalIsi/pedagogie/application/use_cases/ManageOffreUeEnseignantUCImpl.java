package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.AssignEnseignantClassesDTO;
import com.projetTransversalIsi.pedagogie.application.dto.OffreUeResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.exceptions.OffreUeNotFoundException;
import com.projetTransversalIsi.pedagogie.infrastructure.OffreUeMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.EnseignantClasseLink;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataOffreUeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageOffreUeEnseignantUCImpl {

    private final SpringDataOffreUeRepository offreUeRepo;
    private final OffreUeMapper mapper;

    @Transactional
    public OffreUeResponseDTO assignClasses(Long offreUeId, AssignEnseignantClassesDTO dto) {
        var entity = offreUeRepo.findById(offreUeId)
                .orElseThrow(() -> new OffreUeNotFoundException(offreUeId));
        dto.classeIds().forEach(classeId ->
                entity.getEnseignantAssignments().add(new EnseignantClasseLink(dto.enseignantId(), classeId)));
        return mapper.toResponseDTO(mapper.toDomain(offreUeRepo.save(entity)));
    }

    @Transactional
    public OffreUeResponseDTO removeClasse(Long offreUeId, Long enseignantId, Long classeId) {
        var entity = offreUeRepo.findById(offreUeId)
                .orElseThrow(() -> new OffreUeNotFoundException(offreUeId));
        entity.getEnseignantAssignments()
                .removeIf(l -> l.getEnseignantId().equals(enseignantId) && l.getClasseId().equals(classeId));
        return mapper.toResponseDTO(mapper.toDomain(offreUeRepo.save(entity)));
    }

    @Transactional
    public OffreUeResponseDTO removeEnseignant(Long offreUeId, Long enseignantId) {
        var entity = offreUeRepo.findById(offreUeId)
                .orElseThrow(() -> new OffreUeNotFoundException(offreUeId));
        entity.getEnseignantAssignments().removeIf(l -> l.getEnseignantId().equals(enseignantId));
        return mapper.toResponseDTO(mapper.toDomain(offreUeRepo.save(entity)));
    }
}
