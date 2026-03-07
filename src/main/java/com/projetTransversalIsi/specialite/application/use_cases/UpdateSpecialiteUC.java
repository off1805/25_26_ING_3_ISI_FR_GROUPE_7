package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.application.dto.UpdateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.domain.Specialite;

public interface UpdateSpecialiteUC {
    Specialite execute(Long id, UpdateSpecialiteRequestDTO command);
}
