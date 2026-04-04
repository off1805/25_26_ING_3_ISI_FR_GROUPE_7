package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;

import java.util.List;
import java.util.Optional;

public interface PresenceListRepository {
    PresenceList save(PresenceList presenceList);
    Optional<PresenceList> findById(Long id);
    List<PresenceList> findBySeanceId(Long seanceId);
    List<PresenceList> findByClasseId(Long classeId);
    void delete(PresenceList presenceList);
}
