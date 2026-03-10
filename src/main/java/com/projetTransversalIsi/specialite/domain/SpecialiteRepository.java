package com.projetTransversalIsi.specialite.domain;

import java.util.List;
import java.util.Optional;

public interface SpecialiteRepository {

    Specialite save(Specialite specialite);

    Optional<Specialite> findById(Long id);

    Optional<Specialite> findByCode(String code);

    boolean existsByCode(String code);

    List<Specialite> findAll();

    List<Specialite> findAllActive();

    List<Specialite> findByBrancheCode(String brancheCode);

    List<Specialite> findByBrancheCodeAndNiveauMinimumLessThanEqual(String brancheCode, int niveau);

    void deleteById(Long id);
}
