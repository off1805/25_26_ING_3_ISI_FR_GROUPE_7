package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;

import java.util.List;

public interface GetSpecialitesByBrancheUC {
    List<Specialite> execute(String brancheCode, Integer niveau);
}
