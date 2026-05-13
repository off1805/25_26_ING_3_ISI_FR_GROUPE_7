package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

// Port secondaire pour les séances. Implémenté par JpaSeanceRepository.
public interface SeanceRepository {

    Seance save(Seance seance);
    Optional<Seance> findById(Long id);
    List<Seance> findAll();
    // delete = hard delete, appelé lors du remplacement total des séances d'un emploi (updateWithSeances).
    void delete(Seance seance);

    Optional<Seance> findActiveById(Long id);
    List<Seance> findAllActive();
    List<Seance> findAllDeleted();

    List<Seance> findByDate(LocalDate date);
    List<Seance> findByEnseignantId(Long enseignantId);
    List<Seance> findByCoursId(Long coursId);

    // Détecte si un enseignant a déjà une séance active chevauchant le créneau demandé.
    // Algorithme d'overlap : heureDebut_existante < heureFin_nouvelle ET heureFin_existante > heureDebut_nouvelle.
    boolean existsConflict(Long enseignantId, LocalDate date, LocalTime heureDebut, LocalTime heureFin);
}
