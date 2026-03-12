package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;

import java.util.List;

public interface GetSpecialitesByNiveauUC {
    List<Specialite> execute(Long niveauId);
}
