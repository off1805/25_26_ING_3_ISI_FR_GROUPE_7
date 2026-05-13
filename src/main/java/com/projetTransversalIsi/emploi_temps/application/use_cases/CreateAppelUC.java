package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AppelResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAppelDTO;

public interface CreateAppelUC {
    AppelResponseDTO execute(CreateAppelDTO dto);
}
