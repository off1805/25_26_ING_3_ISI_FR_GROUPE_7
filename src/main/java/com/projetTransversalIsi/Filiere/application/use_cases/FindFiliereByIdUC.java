package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
public interface FindFiliereByIdUC {
    Filiere execute(Long id);
}
