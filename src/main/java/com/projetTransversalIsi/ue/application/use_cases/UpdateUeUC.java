package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.application.dto.UpdateUeRequestDTO;

public interface UpdateUeUC {
    Ue execute(Long id, UpdateUeRequestDTO command);
}
