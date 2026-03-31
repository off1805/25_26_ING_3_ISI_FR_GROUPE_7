package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.application.dto.UpdateUeRequestDTO;

public interface UpdateUeUC {
    Ue execute(Long id, UpdateUeRequestDTO command);
}
