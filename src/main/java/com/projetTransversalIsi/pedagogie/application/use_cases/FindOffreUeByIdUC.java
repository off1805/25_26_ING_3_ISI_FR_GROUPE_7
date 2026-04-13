package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;

public interface FindOffreUeByIdUC {
    OffreUe execute(Long id);
}
