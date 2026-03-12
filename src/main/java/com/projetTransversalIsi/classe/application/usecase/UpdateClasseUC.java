package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.application.dto.UpdateClasseRequestDTO;
import com.projetTransversalIsi.classe.domain.Classe;

public interface UpdateClasseUC {
    Classe execute(Long id, UpdateClasseRequestDTO command);
}
