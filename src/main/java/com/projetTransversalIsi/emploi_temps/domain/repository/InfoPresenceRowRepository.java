package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;

import java.util.List;
import java.util.Optional;

public interface InfoPresenceRowRepository {
    InfoPresenceRow save(InfoPresenceRow row);
    Optional<InfoPresenceRow> findById(Long id);
    List<InfoPresenceRow> findByPresenceRowId(Long presenceRowId);
    List<InfoPresenceRow> findByPresenceRowIdIn(List<Long> presenceRowIds);
    List<InfoPresenceRow> findByAppelId(Long appelId);
    // Étudiants non encore déterminés pour un appel donné (isPresent = null).
    List<InfoPresenceRow> findByAppelIdAndIsPresentIsNull(Long appelId);
    // Identifiants distincts des appels ayant encore des lignes en attente (isPresent = null).
    List<Long> findDistinctAppelIdsWithPendingRows();
    void delete(Long id);
}
