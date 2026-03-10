package com.projetTransversalIsi.seance.infrastructure;

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

    // Recherches simples
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


    @Query("SELECT COUNT(s) > 0 FROM JpaSeanceEntity s " +
            "WHERE s.salle = :salle " +
            "AND s.dateSeance = :date " +
            "AND ((s.heureDebut < :heureFin AND s.heureFin > :heureDebut)) " +
            "AND s.deleted = false")
    boolean existsConflictForSalle(
            @Param("salle") String salle,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );
}