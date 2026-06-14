package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataEmploiTempsRepository extends JpaRepository<JpaEmploiTempsEntity, Long>, JpaSpecificationExecutor<JpaEmploiTempsEntity> {


    // Filtré sur l'année scolaire active : les emplois du temps des années précédentes
    // ne doivent plus apparaître dans les vues "courantes" (dashboard surveillant, étudiant).
    @Query("SELECT e FROM JpaEmploiTempsEntity e WHERE e.classeId = :classeId " +
            "AND e.anneeScolaireId = (SELECT a.id FROM JpaAnneeScolaireEntity a WHERE a.active = true)")
    List<JpaEmploiTempsEntity> findByClasseId(@Param("classeId") Long classeId);

    List<JpaEmploiTempsEntity> findBySemaine(Integer semaine);

    // Retourne les emplois dont la fenêtre [dateDebut, dateFin] englobe la date donnée,
    // limités à l'année scolaire active.
    @Query("SELECT e FROM JpaEmploiTempsEntity e WHERE " +
            ":date BETWEEN e.dateDebut AND e.dateFin " +
            "AND e.anneeScolaireId = (SELECT a.id FROM JpaAnneeScolaireEntity a WHERE a.active = true)")
    List<JpaEmploiTempsEntity> findByPeriode(@Param("date") LocalDate date);

    Optional<JpaEmploiTempsEntity> findByIdAndDeletedFalse(Long id);
    List<JpaEmploiTempsEntity> findByDeletedFalse();
    List<JpaEmploiTempsEntity> findByDeletedTrue();

    // Détecte un chevauchement de période pour une classe : empêche deux emplois du temps
    // d'une même classe de se superposer (même logique d'overlap que pour les séances).
    @Query("SELECT COUNT(e) > 0 FROM JpaEmploiTempsEntity e " +
            "WHERE e.classeId = :classeId " +
            "AND ((e.dateDebut <= :dateFin AND e.dateFin >= :dateDebut)) " +
            "AND e.deleted = false")
    boolean existsEmploiForPeriode(
            @Param("classeId") Long classeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

    // Remonte l'emploi du temps propriétaire d'une séance via la jointure sur la collection seances.
    @Query("SELECT e FROM JpaEmploiTempsEntity e JOIN e.seances s WHERE s.id = :seanceId AND e.deleted = false")
    Optional<JpaEmploiTempsEntity> findBySeanceId(@Param("seanceId") Long seanceId);
}
