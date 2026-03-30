package com.projetTransversalIsi.structure_academique.domain.repository;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import java.util.List;
import java.util.Optional;

public interface CycleRepository {
    Cycle save(Cycle cycle);
    Optional<Cycle> findById(Long id);
    Optional<Cycle> findByCode(String code);
    List<Cycle> findAll();
    void delete(Long id);
    boolean existsByCode(String code);
    boolean existsByName(String name);
    List<Cycle> findAllActive();
    List<Cycle> findAllDeleted();
    List<Cycle> findAllByDeletedFalse();
}
