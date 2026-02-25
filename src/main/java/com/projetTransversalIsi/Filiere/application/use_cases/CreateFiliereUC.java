package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.CreateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;

public interface CreateFiliereUC {
    Filiere execute(CreateFiliereRequestDTO command);
}
