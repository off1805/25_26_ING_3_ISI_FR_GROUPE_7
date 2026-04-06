package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;

import java.util.List;

public interface FindSemestresByAnneeScolaireUC {
    List<SemestreResponseDTO> execute(Long anneeScolaireId, Long specialiteId);
}