package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Semestre;
import com.projetTransversalIsi.pedagogie.infrastructure.SemestreMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataSemestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindSemestresByAnneeScolaireUCImpl implements FindSemestresByAnneeScolaireUC {

    private final SpringDataSemestreRepository semestreRepository;
    private final SemestreMapper semestreMapper;

    @Override
    public List<SemestreResponseDTO> execute(Long anneeScolaireId, Long specialiteId) {
        List<Semestre> semestres = semestreRepository
                .findByAnneeScolaireIdAndSpecialiteIdOrderByNumeroAsc(anneeScolaireId, specialiteId)
                .stream()
                .map(semestreMapper::toDomain)
                .toList();

        return semestreMapper.toResponseDTOList(semestres);
    }
}