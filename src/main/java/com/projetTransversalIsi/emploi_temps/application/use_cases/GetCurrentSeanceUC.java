package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;

import java.util.Optional;

public interface GetCurrentSeanceUC {
    Optional<SeanceResponseDTO> execute(Long enseignantId);
}
