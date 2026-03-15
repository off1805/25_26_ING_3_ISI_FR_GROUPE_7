package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.domain.Niveau;
import java.util.List;

public interface FindNiveauxByFiliereIdUC {
    List<Niveau> execute(Long filiereId);
}
