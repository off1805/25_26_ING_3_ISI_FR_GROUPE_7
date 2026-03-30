package com.projetTransversalIsi.structure_academique.domain.repository;

import com.projetTransversalIsi.structure_academique.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import java.util.List;
import java.util.Optional;

public interface NiveauRepository {
    Niveau save(Niveau niveau);
    Optional<Niveau> findById(Long id);
    List<Niveau> findAll(SearchNiveauRequestDTO criteria);
    void delete(Niveau niveau);
    Optional<Niveau> findActiveById(Long id);
    boolean existsByOrdreAndFiliereId(int ordre, Long filiereId);
    boolean existsByOrdreAndFiliereIdAndDeletedFalse(int ordre, Long filiereId);
    List<Niveau> findByFiliereId(Long filiereId);
}
