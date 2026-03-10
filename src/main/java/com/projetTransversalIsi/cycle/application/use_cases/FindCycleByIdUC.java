package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.domain.Cycle;

public interface FindCycleByIdUC {
    Cycle execute(Long id);
}
