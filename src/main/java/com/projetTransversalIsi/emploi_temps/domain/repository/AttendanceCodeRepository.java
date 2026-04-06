package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;

import java.util.List;
import java.util.Optional;

public interface AttendanceCodeRepository {
    AttendanceCode save(AttendanceCode code);
    Optional<AttendanceCode> findById(Long id);
    List<AttendanceCode> findBySeanceId(Long seanceId);
    List<AttendanceCode> findByEnseignantId(Long enseignantId);
    void delete(Long id);
}
