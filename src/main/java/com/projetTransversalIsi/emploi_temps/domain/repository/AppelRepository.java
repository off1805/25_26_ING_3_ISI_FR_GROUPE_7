package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.Appel;

import java.util.List;
import java.util.Optional;

public interface AppelRepository {
    Appel save(Appel appel);
    Optional<Appel> findById(Long id);
    List<Appel> findByPresenceListId(Long presenceListId);
    // Utilisé par MarkStudentPresentUCImpl pour retrouver l'Appel à partir du code QR/PIN scanné.
    Optional<Appel> findByAttendanceCodeId(Long attendanceCodeId);
    void delete(Long id);
}
