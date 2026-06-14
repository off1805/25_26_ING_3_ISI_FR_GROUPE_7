package com.projetTransversalIsi.justificatif.infrastructure.repository;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.infrastructure.entity.JpaJustificatifEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJustificatifRepository extends JpaRepository<JpaJustificatifEntity, Long> {

    List<JpaJustificatifEntity> findByEtudiantId(Long etudiantId);

    // Jointure sur la collection @ElementCollection seanceIds via JPQL MEMBER OF.
    @Query("SELECT DISTINCT j FROM JpaJustificatifEntity j JOIN j.seanceIds s " +
           "WHERE j.etudiantId = :etudiantId AND s = :seanceId")
    List<JpaJustificatifEntity> findByEtudiantIdAndSeanceId(
            @Param("etudiantId") Long etudiantId,
            @Param("seanceId") Long seanceId);

    @Query("SELECT j FROM JpaJustificatifEntity j WHERE j.anneeScolaireId = :anneeScolaireId" +
           " AND j.etudiantId IN :etudiantIds" +
           " AND (:statut IS NULL OR j.statut = :statut)" +
           " ORDER BY j.createdAt DESC")
    Page<JpaJustificatifEntity> findArchive(
            @Param("anneeScolaireId") Long anneeScolaireId,
            @Param("etudiantIds") List<Long> etudiantIds,
            @Param("statut") Justificatif.Statut statut,
            Pageable pageable);
}
