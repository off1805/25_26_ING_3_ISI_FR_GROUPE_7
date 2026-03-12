package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.application.dto.CreateNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;

public interface CreateNiveauUC {
    Niveau execute(CreateNiveauRequestDTO request);
}
