package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.UpdateEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface UpdateEmploiTempsUC {
    EmploiTemps execute(UpdateEmploiTempsRequestDTO command);
}