package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.UpdateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;

public interface UpdateOffreUeUC {
    OffreUe execute(Long id, UpdateOffreUeRequestDTO command);
}
