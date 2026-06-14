package com.projetTransversalIsi.web_application.ap.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringDataAPDashboardRepository extends JpaRepository<JpaPresenceRowEntity, Long> {

    @Query(nativeQuery = true, value =
        "SELECT COUNT(sp.id) FROM student_profile sp" +
        " WHERE sp.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite s ON s.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )"
    )
    Long countEtudiantsByFiliereId(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT COUNT(DISTINCT s.id) FROM seance s" +
        " INNER JOIN emploi_temps et ON s.emploi_temps_id = et.id" +
        " WHERE et.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite spec ON spec.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = spec.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " AND s.deleted = false" +
        " AND et.deleted = false" +
        " AND s.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND s.date_seance >= :startOfWeek" +
        " AND s.date_seance <= :endOfWeek"
    )
    Long countSeancesSemaine(
            @Param("filiereId") Long filiereId,
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek);

    @Query(nativeQuery = true, value =
        "SELECT COUNT(j.id) FROM justificatif j" +
        " WHERE j.statut = 'PENDING'" +
        " AND j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND j.etudiant_id IN (" +
        "   SELECT sp.id FROM student_profile sp" +
        "   WHERE sp.classe_id IN (" +
        "     SELECT c.id FROM classe c" +
        "     INNER JOIN specialite s ON s.id = c.specialite_id" +
        "     INNER JOIN niveau n ON n.id = s.niveau_id" +
        "     WHERE n.filiere_id = :filiereId" +
        "   )" +
        " )"
    )
    Long countJustificatifsEnAttente(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT CASE WHEN COUNT(pr.id) = 0 THEN 100.0" +
        "            ELSE SUM(CASE WHEN pr.present = true THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(pr.id)" +
        "       END" +
        " FROM presence_row pr" +
        " INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        " WHERE pl.deleted = false" +
        " AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND pl.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite s ON s.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )"
    )
    Double computeTauxPresence(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT c.code AS classeCode, COUNT(pr.id) AS nbAbsences" +
        " FROM presence_row pr" +
        " INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        " INNER JOIN classe c ON c.id = pl.classe_id" +
        " WHERE pl.deleted = false" +
        " AND (pr.present IS NULL OR pr.present = false)" +
        " AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND pl.classe_id IN (" +
        "   SELECT c2.id FROM classe c2" +
        "   INNER JOIN specialite s ON s.id = c2.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " GROUP BY c.id, c.code" +
        " ORDER BY nbAbsences DESC"
    )
    List<ClasseAbsenceRow> findAbsencesParClasse(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT j.statut AS statut, COUNT(j.id) AS cnt" +
        " FROM justificatif j" +
        " WHERE j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND j.etudiant_id IN (" +
        "   SELECT sp.id FROM student_profile sp" +
        "   WHERE sp.classe_id IN (" +
        "     SELECT c.id FROM classe c" +
        "     INNER JOIN specialite s ON s.id = c.specialite_id" +
        "     INNER JOIN niveau n ON n.id = s.niveau_id" +
        "     WHERE n.filiere_id = :filiereId" +
        "   )" +
        " )" +
        " GROUP BY j.statut"
    )
    List<StatutCountRow> findJustificatifsParStatut(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT c.code AS classeCode, CAST(pl.date AS CHAR) AS jour, COUNT(pr.id) AS nbAbsences" +
        " FROM presence_row pr" +
        " INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        " INNER JOIN classe c ON c.id = pl.classe_id" +
        " WHERE pl.deleted = false" +
        " AND (pr.present IS NULL OR pr.present = false)" +
        " AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND pl.classe_id IN (" +
        "   SELECT c2.id FROM classe c2" +
        "   INNER JOIN specialite s ON s.id = c2.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " GROUP BY c.id, c.code, pl.date" +
        " ORDER BY pl.date, c.code"
    )
    List<AbsenceEvolutionRow> findAbsencesEvolution(@Param("filiereId") Long filiereId);
}
