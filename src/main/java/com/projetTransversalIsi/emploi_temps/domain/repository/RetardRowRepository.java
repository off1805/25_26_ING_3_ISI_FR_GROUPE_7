package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.RetardRow;

import java.util.List;
import java.util.Optional;

public interface RetardRowRepository {
    RetardRow save(RetardRow retardRow);
    Optional<RetardRow> findById(Long id);
    List<RetardRow> findByRetardListId(Long retardListId);
    void delete(RetardRow retardRow);
}
