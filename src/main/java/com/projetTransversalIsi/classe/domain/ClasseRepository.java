package com.projetTransversalIsi.classe.domain;

import java.util.List;
import java.util.Optional;

public interface ClasseRepository {
    Classe save(Classe classe);
    Optional<Classe> findById(Long id);
    Optional<Classe> findByCode(String code);
    List<Classe> findAll();
    List<Classe> findBySpecialiteId(Long specialiteId);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
