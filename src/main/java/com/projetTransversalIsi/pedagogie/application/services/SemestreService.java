package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;

import java.util.List;

public interface SemestreService {
    SemestreResponseDTO createSemestre(CreateSemestreRequestDTO request);
    List<SemestreResponseDTO> getSemestresByAnneeScolaireAndSpecialite(Long anneeScolaireId, Long specialiteId);
}