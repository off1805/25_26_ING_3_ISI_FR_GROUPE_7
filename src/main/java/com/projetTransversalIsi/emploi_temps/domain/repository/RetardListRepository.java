package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RetardListRepository {
    RetardList save(RetardList retardList);
    Optional<RetardList> findById(Long id);
    Optional<RetardList> findByClasseIdAndSemaineDebutAndDeletedFalse(Long classeId, LocalDate semaineDebut);
    List<RetardList> findByClasseId(Long classeId);
    void delete(RetardList retardList);
}
