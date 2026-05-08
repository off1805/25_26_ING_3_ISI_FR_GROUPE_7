package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;

import java.util.List;
import java.util.Optional;

public interface PresenceRowRepository {
    PresenceRow save(PresenceRow presenceRow);
    Optional<PresenceRow> findById(Long id);
    List<PresenceRow> findByPresenceListId(Long presenceListId);
    List<PresenceRow> findByEtudiantId(Long etudiantId);
    void delete(PresenceRow presenceRow);
}
