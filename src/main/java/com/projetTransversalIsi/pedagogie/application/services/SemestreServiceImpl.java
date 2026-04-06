package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;
import com.projetTransversalIsi.pedagogie.application.use_cases.CreateSemestreUC;
import com.projetTransversalIsi.pedagogie.application.use_cases.FindSemestresByAnneeScolaireUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemestreServiceImpl implements SemestreService {

    private final CreateSemestreUC createSemestreUC;
    private final FindSemestresByAnneeScolaireUC findSemestresByAnneeScolaireUC;

    @Override
    public SemestreResponseDTO createSemestre(CreateSemestreRequestDTO request) {
        return createSemestreUC.execute(request);
    }

    @Override
    public List<SemestreResponseDTO> getSemestresByAnneeScolaireAndSpecialite(Long anneeScolaireId, Long specialiteId) {
        return findSemestresByAnneeScolaireUC.execute(anneeScolaireId, specialiteId);
    }
}