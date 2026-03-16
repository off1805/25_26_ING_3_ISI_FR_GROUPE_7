package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.UpdateEmploiTempsWithSeancesDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface UpdateEmploiTempsWithSeancesUC {
    EmploiTemps execute(UpdateEmploiTempsWithSeancesDTO command);
}
