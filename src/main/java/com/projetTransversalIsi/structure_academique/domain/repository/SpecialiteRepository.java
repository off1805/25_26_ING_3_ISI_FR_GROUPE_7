package com.projetTransversalIsi.structure_academique.domain.repository;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import java.util.List;
import java.util.Optional;

public interface SpecialiteRepository {

    Specialite save(Specialite specialite);

    Optional<Specialite> findById(Long id);

    Optional<Specialite> findByCode(String code);

    boolean existsByCode(String code);

    List<Specialite> findAll();

    List<Specialite> findAllActive();

    List<Specialite> findByNiveauId(Long niveauId);

    void deleteById(Long id);
}
