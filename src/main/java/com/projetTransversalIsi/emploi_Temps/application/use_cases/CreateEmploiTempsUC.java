package com.projetTransversalIsi.emploi_Temps.application.use_cases;


import com.projetTransversalIsi.emploi_Temps.application.dto.CreateEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;

public interface CreateEmploiTempsUC {
    EmploiTemps execute(CreateEmploiTempsRequestDTO command);
}