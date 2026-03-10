package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;

@FunctionalInterface
public interface FindSpecialiteByIdUC {
    Specialite execute(Long id);
}
