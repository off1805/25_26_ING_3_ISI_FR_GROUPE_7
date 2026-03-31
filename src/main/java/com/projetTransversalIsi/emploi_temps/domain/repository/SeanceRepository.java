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

    // -------------------------------------------------------------------------
    // Nouvelles méthodes — requises par les use cases professeur
    // -------------------------------------------------------------------------

    /**
     * Retourne les séances d'un enseignant pour un jour précis.
     *
     * @param enseignantId identifiant de l'enseignant
     * @param date         jour cible
     * @return liste des séances (incluant les soft-deleted ; le filtrage est à la charge du use case)
     */
    List<Seance> findByEnseignantIdAndDate(Long enseignantId, LocalDate date);

    /**
     * Retourne les séances d'un enseignant comprises entre {@code debut} et {@code fin} (inclus).
     *
     * @param enseignantId identifiant de l'enseignant
     * @param debut        premier jour de la période (ex : lundi de la semaine)
     * @param fin          dernier jour de la période  (ex : dimanche de la semaine)
     * @return liste des séances (incluant les soft-deleted ; le filtrage est à la charge du use case)
     */
    List<Seance> findByEnseignantIdAndDateBetween(Long enseignantId, LocalDate debut, LocalDate fin);
}
