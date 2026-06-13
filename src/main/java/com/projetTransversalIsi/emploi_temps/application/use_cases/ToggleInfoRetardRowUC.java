package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.InfoRetardRowResponseDTO;

public interface ToggleInfoRetardRowUC {
    InfoRetardRowResponseDTO execute(Long infoRetardRowId);
}
