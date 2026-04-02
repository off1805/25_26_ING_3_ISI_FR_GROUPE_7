package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SeanceRepository {

    Seance save(Seance seance);
    Optional<Seance> findById(Long id);
    List<Seance> findAll();
    void delete(Seance seance);

    Optional<Seance> findActiveById(Long id);
    List<Seance> findAllActive();
    List<Seance> findAllDeleted();

    List<Seance> findByDate(LocalDate date);
    List<Seance> findByEnseignantId(Long enseignantId);
    List<Seance> findByCoursId(Long coursId);

    boolean existsConflict(Long enseignantId, LocalDate date, LocalTime heureDebut, LocalTime heureFin);
}
