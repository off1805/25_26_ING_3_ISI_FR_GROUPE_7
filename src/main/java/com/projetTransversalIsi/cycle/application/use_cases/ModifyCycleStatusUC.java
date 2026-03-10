package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.ModifyCycleStatusDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;

public interface ModifyCycleStatusUC {
    Cycle execute(Long id, ModifyCycleStatusDTO statusDTO);
}
