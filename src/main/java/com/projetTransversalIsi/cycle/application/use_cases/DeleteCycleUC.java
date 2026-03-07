package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.DeleteCycleRequestDTO;

public interface DeleteCycleUC {
    void execute(DeleteCycleRequestDTO request);
}
