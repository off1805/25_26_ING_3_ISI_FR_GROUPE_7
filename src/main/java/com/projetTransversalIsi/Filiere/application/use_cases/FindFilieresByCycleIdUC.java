package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import java.util.List;

public interface FindFilieresByCycleIdUC {
    List<Filiere> execute(Long cycleId);
}
