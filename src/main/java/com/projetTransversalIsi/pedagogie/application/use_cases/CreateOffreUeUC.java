package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;

public interface CreateOffreUeUC {
    OffreUe execute(CreateOffreUeRequestDTO command);
}
