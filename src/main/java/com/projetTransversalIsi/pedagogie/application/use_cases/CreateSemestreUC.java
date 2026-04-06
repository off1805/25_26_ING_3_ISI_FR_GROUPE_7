package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;

public interface CreateSemestreUC {
    SemestreResponseDTO execute(CreateSemestreRequestDTO request);
}