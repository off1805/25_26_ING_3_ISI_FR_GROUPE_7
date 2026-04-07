package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Semestre;
import com.projetTransversalIsi.pedagogie.infrastructure.SemestreMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataSemestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSemestreUCImpl implements CreateSemestreUC {

    private final SpringDataSemestreRepository semestreRepository;
    private final SemestreMapper semestreMapper;

    @Override
    public SemestreResponseDTO execute(CreateSemestreRequestDTO request) {

        if (request.numero() == null || (request.numero() != 1 && request.numero() != 2)) {
            throw new IllegalArgumentException("Le numéro du semestre doit être 1 ou 2.");
        }

        boolean exists = semestreRepository.existsByAnneeScolaireIdAndSpecialiteIdAndNumero(
                request.anneeScolaireId(),
                request.specialiteId(),
                request.numero()
        );

        if (exists) {
            throw new IllegalStateException(
                    "Ce semestre existe déjà pour cette année scolaire et cette spécialité."
            );
        }

        Semestre semestre = new Semestre();
        semestre.setNumero(request.numero());
        semestre.setLibelle("Semestre " + request.numero());
        semestre.setAnneeScolaireId(request.anneeScolaireId());
        semestre.setSpecialiteId(request.specialiteId());

        JpaSemestreEntity saved = semestreRepository.save(semestreMapper.toEntity(semestre));

        return semestreMapper.toResponseDTO(semestreMapper.toDomain(saved));
    }
}