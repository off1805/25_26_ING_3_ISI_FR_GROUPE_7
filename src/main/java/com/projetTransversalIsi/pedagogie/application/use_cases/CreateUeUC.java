package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.application.dto.CreateUeRequestDTO;

public interface CreateUeUC {
    Ue execute(CreateUeRequestDTO command);
}
