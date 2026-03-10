package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.application.dto.CreateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.domain.Specialite;

public interface CreateSpecialiteUC {
    Specialite execute(CreateSpecialiteRequestDTO command);
}
