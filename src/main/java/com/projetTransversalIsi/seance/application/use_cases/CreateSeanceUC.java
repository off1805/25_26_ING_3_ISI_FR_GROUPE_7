package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.CreateSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;

public interface CreateSeanceUC {
    Seance execute(CreateSeanceRequestDTO command);
}