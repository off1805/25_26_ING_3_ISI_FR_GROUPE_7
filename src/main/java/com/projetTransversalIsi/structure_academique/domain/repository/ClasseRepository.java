package com.projetTransversalIsi.structure_academique.domain.repository;

import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import java.util.List;
import java.util.Optional;

public interface ClasseRepository {
    Classe save(Classe classe);
    Optional<Classe> findById(Long id);
    Optional<Classe> findByCode(String code);
    List<Classe> findAll();
    void deleteById(Long id);
    boolean existsByCode(String code);
    List<Classe> findBySpecialiteId(Long specialiteId);
}
