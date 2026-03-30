package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.Ue;

@FunctionalInterface
public interface FindUeByIdUC {
    Ue execute(Long id);
}
