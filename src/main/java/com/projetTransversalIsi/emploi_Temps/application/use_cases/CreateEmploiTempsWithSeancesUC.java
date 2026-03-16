package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.CreateEmploiTempsWithSeancesDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface CreateEmploiTempsWithSeancesUC {
    EmploiTemps execute(CreateEmploiTempsWithSeancesDTO command);
}
