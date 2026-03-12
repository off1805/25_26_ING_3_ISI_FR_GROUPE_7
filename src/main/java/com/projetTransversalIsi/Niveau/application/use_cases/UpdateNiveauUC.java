package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.application.dto.UpdateNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;

public interface UpdateNiveauUC {
    Niveau execute(UpdateNiveauRequestDTO request);
}
