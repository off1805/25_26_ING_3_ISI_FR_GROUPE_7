package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.application.dto.CreateUeRequestDTO;

public interface CreateUeUC {
    Ue execute(CreateUeRequestDTO command);
}
