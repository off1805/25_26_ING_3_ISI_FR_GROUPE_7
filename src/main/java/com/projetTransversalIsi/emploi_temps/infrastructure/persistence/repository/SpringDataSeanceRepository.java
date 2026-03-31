package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaSeanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataSeanceRepository extends JpaRepository<JpaSeanceEntity, Long> {

    // Recherches simples existantes
    List<JpaSeanceEntity> findByDateSeance(LocalDate date);
    List<JpaSeanceEntity> findByEnseignantId(Long enseignantId);
    List<JpaSeanceEntity> findByCoursId(Long coursId);

    // Soft delete
    Optional<JpaSeanceEntity> findByIdAndDeletedFalse(Long id);
    List<JpaSeanceEntity> findByDeletedFalse();
    List<JpaSeanceEntity> findByDeletedTrue();

    @Query("SELECT COUNT(s) > 0 FROM JpaSeanceEntity s " +
            "WHERE s.enseignantId = :enseignantId " +
            "AND s.dateSeance = :date " +
            "AND ((s.heureDebut < :heureFin AND s.heureFin > :heureDebut)) " +
            "AND s.deleted = false")
    boolean existsConflictForEnseignant(
            @Param("enseignantId") Long enseignantId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    // -------------------------------------------------------------------------
    // Nouvelles méthodes — use cases professeur
    // -------------------------------------------------------------------------

    /**
     * Séances d'un enseignant pour un jour précis (toutes, y compris soft-deleted).
     */
    List<JpaSeanceEntity> findByEnseignantIdAndDateSeance(Long enseignantId, LocalDate dateSeance);

    /**
     * Séances d'un enseignant entre deux dates incluses (toutes, y compris soft-deleted).
     */
    List<JpaSeanceEntity> findByEnseignantIdAndDateSeanceBetween(
            Long enseignantId,
            LocalDate debut,
            LocalDate fin
    );
}
