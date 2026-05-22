package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.AssignEnseignantDTO;
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
    public OffreUeResponseDTO assign(Long offreUeId, AssignEnseignantDTO dto) {
        var entity = offreUeRepo.findById(offreUeId)
                .orElseThrow(() -> new OffreUeNotFoundException(offreUeId));
        entity.getEnseignantAssignments().add(new EnseignantClasseLink(dto.enseignantId(), dto.classeId()));
        return mapper.toResponseDTO(mapper.toDomain(offreUeRepo.save(entity)));
    }

    @Transactional
    public OffreUeResponseDTO remove(Long offreUeId, AssignEnseignantDTO dto) {
        var entity = offreUeRepo.findById(offreUeId)
                .orElseThrow(() -> new OffreUeNotFoundException(offreUeId));
        entity.getEnseignantAssignments()
                .removeIf(l -> l.getEnseignantId().equals(dto.enseignantId())
                            && l.getClasseId().equals(dto.classeId()));
        return mapper.toResponseDTO(mapper.toDomain(offreUeRepo.save(entity)));
    }
}
