package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.SearchEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import java.util.List;

public interface SearchEmploiTempsUC {
    List<EmploiTemps> execute(SearchEmploiTempsRequestDTO criteria);
}