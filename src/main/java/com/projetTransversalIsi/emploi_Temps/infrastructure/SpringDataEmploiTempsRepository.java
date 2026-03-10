package com.projetTransversalIsi.emploi_Temps.infrastructure;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataEmploiTempsRepository extends JpaRepository<JpaEmploiTempsEntity, Long> {


    List<JpaEmploiTempsEntity> findByFiliereId(Long filiereId);
    List<JpaEmploiTempsEntity> findByNiveauId(Long niveauId);
    List<JpaEmploiTempsEntity> findBySemaine(Integer semaine);

    @Query("SELECT e FROM JpaEmploiTempsEntity e WHERE " +
            ":date BETWEEN e.dateDebut AND e.dateFin")
    List<JpaEmploiTempsEntity> findByPeriode(@Param("date") LocalDate date);


    Optional<JpaEmploiTempsEntity> findByIdAndDeletedFalse(Long id);
    List<JpaEmploiTempsEntity> findByDeletedFalse();
    List<JpaEmploiTempsEntity> findByDeletedTrue();


    @Query("SELECT COUNT(e) > 0 FROM JpaEmploiTempsEntity e " +
            "WHERE e.filiereId = :filiereId " +
            "AND e.niveauId = :niveauId " +
            "AND ((e.dateDebut <= :dateFin AND e.dateFin >= :dateDebut)) " +
            "AND e.deleted = false")
    boolean existsEmploiForPeriode(
            @Param("filiereId") Long filiereId,
            @Param("niveauId") Long niveauId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM emploi_temps_seance WHERE emploi_temps_id = :emploiId AND seance_id = :seanceId", nativeQuery = true)
    void deleteSeanceFromEmploi(@Param("emploiId") Long emploiId, @Param("seanceId") Long seanceId);
}