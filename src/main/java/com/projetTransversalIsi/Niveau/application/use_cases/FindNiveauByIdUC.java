package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.domain.Niveau;

public interface FindNiveauByIdUC {
    Niveau execute(Long id);
}
