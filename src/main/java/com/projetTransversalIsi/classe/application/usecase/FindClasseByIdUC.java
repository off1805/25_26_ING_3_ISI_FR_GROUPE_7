package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.Classe;

public interface FindClasseByIdUC {
    Classe execute(Long id);
}
