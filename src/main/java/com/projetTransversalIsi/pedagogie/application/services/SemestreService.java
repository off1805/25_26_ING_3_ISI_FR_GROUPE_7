package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;

import java.util.List;
import java.util.Optional;

public interface SemestreService {
    SemestreResponseDTO createSemestre(CreateSemestreRequestDTO request);
    List<SemestreResponseDTO> getSemestresByAnneeScolaireAndNiveau(Long anneeScolaireId, Long niveauId);

    /** Retourne le semestre dont la période couvre la date du jour pour un niveau donné. */
    Optional<SemestreResponseDTO> getActiveSemestre(Long niveauId);
}