package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.CreateCycleRequestDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;

public interface CreateCycleUC {
    Cycle execute(CreateCycleRequestDTO request);
}
