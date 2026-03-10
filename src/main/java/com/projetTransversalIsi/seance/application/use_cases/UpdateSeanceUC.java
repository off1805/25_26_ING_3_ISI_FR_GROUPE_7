package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.UpdateSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;

public interface UpdateSeanceUC {
    Seance execute(UpdateSeanceRequestDTO command);
}