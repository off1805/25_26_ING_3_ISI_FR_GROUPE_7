package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;

import java.util.List;
import java.util.Optional;

public interface InfoRetardRowRepository {
    InfoRetardRow save(InfoRetardRow row);
    Optional<InfoRetardRow> findById(Long id);
    List<InfoRetardRow> findByRetardRowId(Long retardRowId);
    List<InfoRetardRow> findByRetardRowIdIn(List<Long> retardRowIds);
    void delete(InfoRetardRow row);
}
