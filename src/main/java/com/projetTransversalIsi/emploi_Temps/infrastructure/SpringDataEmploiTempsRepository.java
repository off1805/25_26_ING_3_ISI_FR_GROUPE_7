package com.projetTransversalIsi.emploi_Temps.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataEmploiTempsRepository extends JpaRepository<JpaEmploiTempsEntity, Long> {


    List<JpaEmploiTempsEntity> findByClasseId(Long classeId);
    List<JpaEmploiTempsEntity> findBySemaine(Integer semaine);

    @Query("SELECT e FROM JpaEmploiTempsEntity e WHERE " +
            ":date BETWEEN e.dateDebut AND e.dateFin")
    List<JpaEmploiTempsEntity> findByPeriode(@Param("date") LocalDate date);


    Optional<JpaEmploiTempsEntity> findByIdAndDeletedFalse(Long id);
    List<JpaEmploiTempsEntity> findByDeletedFalse();
    List<JpaEmploiTempsEntity> findByDeletedTrue();


    @Query("SELECT COUNT(e) > 0 FROM JpaEmploiTempsEntity e " +
            "WHERE e.classeId = :classeId " +
            "AND ((e.dateDebut <= :dateFin AND e.dateFin >= :dateDebut)) " +
            "AND e.deleted = false")
    boolean existsEmploiForPeriode(
            @Param("classeId") Long classeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

}
