package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.AddSeanceToEmploiDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface RemoveSeanceFromEmploiUC {
    EmploiTemps execute(AddSeanceToEmploiDTO command);
}