package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.domain.Seance;

public interface FindSeanceByIdUC {
    Seance execute(Long id);
}