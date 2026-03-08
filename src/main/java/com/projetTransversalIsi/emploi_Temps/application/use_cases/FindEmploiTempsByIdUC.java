package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface FindEmploiTempsByIdUC {
    EmploiTemps execute(Long id);
}