package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;

import java.util.List;

@FunctionalInterface
public interface GetAllSpecialitesUC {
    List<Specialite> execute();
}
