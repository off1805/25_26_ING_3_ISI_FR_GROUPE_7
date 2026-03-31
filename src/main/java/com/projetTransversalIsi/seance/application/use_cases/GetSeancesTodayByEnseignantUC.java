package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.domain.Seance;

import java.util.List;

public interface GetSeancesTodayByEnseignantUC {
   
    List<Seance> execute(Long enseignantId, Boolean includeDeleted);
}

