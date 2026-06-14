package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataStudentClasseHistoryRepository extends JpaRepository<JpaStudentClasseHistoryEntity, Long> {
    Optional<JpaStudentClasseHistoryEntity> findByStudentIdAndDateFinIsNull(Long studentId);
    List<JpaStudentClasseHistoryEntity> findByStudentIdOrderByDateDebutDesc(Long studentId);

    @Query("SELECT h FROM JpaStudentClasseHistoryEntity h" +
           " WHERE h.anneeScolaire.id = :anneeScolaireId" +
           " AND h.classe.specialite.niveau.filiere.id = :filiereId" +
           " AND (:classeId IS NULL OR h.classe.id = :classeId)" +
           " ORDER BY h.classe.code, h.student.nom")
    Page<JpaStudentClasseHistoryEntity> findArchive(
            @Param("anneeScolaireId") Long anneeScolaireId,
            @Param("filiereId") Long filiereId,
            @Param("classeId") Long classeId,
            Pageable pageable);
}
