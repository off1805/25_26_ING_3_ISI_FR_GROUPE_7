package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.Classe;
import java.util.List;

public interface FindClassesBySpecialiteIdUC {
    List<Classe> execute(Long specialiteId);
}
