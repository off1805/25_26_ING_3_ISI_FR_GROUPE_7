package com.projetTransversalIsi.Niveau.domain;

import com.projetTransversalIsi.Niveau.application.dto.SearchNiveauRequestDTO;
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
