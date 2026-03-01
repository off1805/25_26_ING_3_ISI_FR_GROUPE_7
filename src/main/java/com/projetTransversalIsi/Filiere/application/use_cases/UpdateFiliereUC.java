package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.UpdateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;

public interface UpdateFiliereUC {
    Filiere execute(UpdateFiliereRequestDTO command);
}
