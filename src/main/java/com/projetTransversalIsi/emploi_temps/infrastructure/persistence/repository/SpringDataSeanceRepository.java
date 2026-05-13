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

    List<JpaSeanceEntity> findByDateSeance(LocalDate date);
    List<JpaSeanceEntity> findByEnseignantId(Long enseignantId);
    List<JpaSeanceEntity> findByCoursId(Long coursId);

    Optional<JpaSeanceEntity> findByIdAndDeletedFalse(Long id);
    List<JpaSeanceEntity> findByDeletedFalse();
    List<JpaSeanceEntity> findByDeletedTrue();

    // Détecte un chevauchement horaire pour un enseignant sur une même journée.
    // Algorithme d'overlap standard : deux intervalles [A,B] et [C,D] se chevauchent
    // si et seulement si A < D ET B > C. Exclut les séances supprimées (deleted=false).
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
}
