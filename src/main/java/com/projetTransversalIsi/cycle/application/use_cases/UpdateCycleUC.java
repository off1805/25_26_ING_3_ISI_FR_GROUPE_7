package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.UpdateCycleRequestDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;

public interface UpdateCycleUC {
    Cycle execute(Long id, UpdateCycleRequestDTO request);
}
