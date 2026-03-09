package com.projetTransversalIsi.level.application.usecase;

import com.projetTransversalIsi.level.application.dto.CreateLevelRequestDTO;
import com.projetTransversalIsi.level.domain.level;

public interface CreateLevelUC {
    level execute(CreateLevelRequestDTO command);
}
