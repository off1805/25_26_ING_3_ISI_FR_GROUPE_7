package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUesForNewYearResponseDTO;

public interface CreateOffreUesForNewYearUC {
    CreateOffreUesForNewYearResponseDTO execute(Long newAnneeScolaireId, Long previousAnneeScolaireId);
}
