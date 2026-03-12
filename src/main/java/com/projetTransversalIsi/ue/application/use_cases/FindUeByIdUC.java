package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;

@FunctionalInterface
public interface FindUeByIdUC {
    Ue execute(Long id);
}
