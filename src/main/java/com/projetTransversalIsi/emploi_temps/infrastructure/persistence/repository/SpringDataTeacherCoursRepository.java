package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.application.dto.TeacherCoursCardRow;
import com.projetTransversalIsi.emploi_temps.application.dto.TeacherEtudiantCoursRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataTeacherCoursRepository extends JpaRepository<JpaPresenceRowEntity, Long> {

    /**
     * Retourne la liste des paires (offreUe, classe) pour un enseignant.
     * Source de vérité : la table offre_ue_enseignant avec classe_id.
     */
    @Query(nativeQuery = true, value =
        "SELECT" +
        "    ou.id                        AS offreUeId," +
        "    ou.libelle                   AS libelle," +
        "    ou.code                      AS code," +
        "    ou.couleur                   AS couleur," +
        "    ou.semestre                  AS semestre," +
        "    ou.credit                    AS credit," +
        "    ou.volume_horaire_total      AS volumeHoraireTotal," +
        "    c.id                         AS classeId," +
        "    c.code                       AS classeCode," +
        "    (SELECT COUNT(*) FROM student_profile sp WHERE sp.classe_id = c.id) AS nbEtudiants," +
        "    (SELECT COUNT(DISTINCT s.id)" +
        "     FROM seance s" +
        "     INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id" +
        "     WHERE s.cours_id = ou.id" +
        "       AND et.classe_id = c.id" +
        "       AND s.deleted = false" +
        "       AND et.deleted = false" +
        "       AND s.date_seance <= CURRENT_DATE) AS nbSeancesFaites" +
        " FROM offre_ue ou" +
        " INNER JOIN offre_ue_enseignant oue ON oue.offre_ue_id = ou.id AND oue.enseignant_id = :enseignantId" +
        " INNER JOIN classe c ON c.id = oue.classe_id" +
        " WHERE ou.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true LIMIT 1)" +
        " ORDER BY ou.semestre, ou.libelle, c.code"
    )
    List<TeacherCoursCardRow> findCoursCardsForEnseignant(@Param("enseignantId") Long enseignantId);

    /**
     * Retourne les statistiques de présence pour chaque étudiant d'une classe
     * pour une offre_ue donnée. Les calculs d'absence tiennent compte de la
     * granularité info_presence_row si disponible.
     */
    @Query(nativeQuery = true, value =
        "SELECT" +
        "    sp.id              AS etudiantId," +
        "    p.nom              AS nom," +
        "    p.prenom           AS prenom," +
        "    p.matricule        AS matricule," +
        "    p.photo_url        AS photoUrl," +
        "    COALESCE(abs_data.nb_absences, 0)       AS nbAbsences," +
        "    COALESCE(abs_data.minutes_absence, 0)   AS minutesAbsence," +
        "    prog.total_minutes                       AS totalMinutesProgrammes" +
        " FROM student_profile sp" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " LEFT JOIN (" +
        "    SELECT pr.etudiant_id," +
        "           COUNT(DISTINCT pr.id) AS nb_absences," +
        "           COALESCE(SUM(CASE" +
        "               WHEN COALESCE(ipr_count.cnt, 0) > 0 THEN COALESCE(ipr_abs.abs_minutes, 0)" +
        "               ELSE TIMESTAMPDIFF(MINUTE, s2.heure_debut, s2.heure_fin)" +
        "           END), 0) AS minutes_absence" +
        "    FROM presence_row pr" +
        "    INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "    INNER JOIN seance s2 ON s2.id = pl.seance_id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id, COUNT(*) AS cnt" +
        "        FROM info_presence_row GROUP BY presence_row_id" +
        "    ) ipr_count ON ipr_count.presence_row_id = pr.id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id," +
        "               SUM(TIMESTAMPDIFF(MINUTE, heure_debut, heure_fin)) AS abs_minutes" +
        "        FROM info_presence_row WHERE is_present = false GROUP BY presence_row_id" +
        "    ) ipr_abs ON ipr_abs.presence_row_id = pr.id" +
        "    WHERE pl.ue_id = :offreUeId" +
        "      AND pl.classe_id = :classeId" +
        "      AND pl.deleted = false" +
        "      AND (pr.present IS NULL OR pr.present = false)" +
        "    GROUP BY pr.etudiant_id" +
        " ) abs_data ON abs_data.etudiant_id = sp.id" +
        " CROSS JOIN (" +
        "    SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)), 0) AS total_minutes" +
        "    FROM seance s" +
        "    INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id" +
        "    WHERE s.cours_id = :offreUeId" +
        "      AND et.classe_id = :classeId" +
        "      AND s.deleted = false" +
        "      AND et.deleted = false" +
        "      AND s.date_seance <= CURRENT_DATE" +
        " ) prog" +
        " WHERE sp.classe_id = :classeId" +
        " ORDER BY p.nom, p.prenom"
    )
    List<TeacherEtudiantCoursRow> findEtudiantsStatsForCours(
            @Param("offreUeId") Long offreUeId,
            @Param("classeId")  Long classeId
    );

    /**
     * Retourne les statistiques de présence d'un étudiant précis pour une matière.
     * Réutilise la même logique de calcul que findEtudiantsStatsForCours.
     */
    @Query(nativeQuery = true, value =
        "SELECT" +
        "    sp.id              AS etudiantId," +
        "    p.nom              AS nom," +
        "    p.prenom           AS prenom," +
        "    p.matricule        AS matricule," +
        "    p.photo_url        AS photoUrl," +
        "    COALESCE(abs_data.nb_absences, 0)       AS nbAbsences," +
        "    COALESCE(abs_data.minutes_absence, 0)   AS minutesAbsence," +
        "    prog.total_minutes                       AS totalMinutesProgrammes" +
        " FROM student_profile sp" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " LEFT JOIN (" +
        "    SELECT pr.etudiant_id," +
        "           COUNT(DISTINCT pr.id) AS nb_absences," +
        "           COALESCE(SUM(CASE" +
        "               WHEN COALESCE(ipr_count.cnt, 0) > 0 THEN COALESCE(ipr_abs.abs_minutes, 0)" +
        "               ELSE TIMESTAMPDIFF(MINUTE, s2.heure_debut, s2.heure_fin)" +
        "           END), 0) AS minutes_absence" +
        "    FROM presence_row pr" +
        "    INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "    INNER JOIN seance s2 ON s2.id = pl.seance_id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id, COUNT(*) AS cnt" +
        "        FROM info_presence_row GROUP BY presence_row_id" +
        "    ) ipr_count ON ipr_count.presence_row_id = pr.id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id," +
        "               SUM(TIMESTAMPDIFF(MINUTE, heure_debut, heure_fin)) AS abs_minutes" +
        "        FROM info_presence_row WHERE is_present = false GROUP BY presence_row_id" +
        "    ) ipr_abs ON ipr_abs.presence_row_id = pr.id" +
        "    WHERE pl.ue_id = :offreUeId" +
        "      AND pl.deleted = false" +
        "      AND (pr.present IS NULL OR pr.present = false)" +
        "      AND pr.etudiant_id = :etudiantId" +
        "    GROUP BY pr.etudiant_id" +
        " ) abs_data ON abs_data.etudiant_id = sp.id" +
        " CROSS JOIN (" +
        "    SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)), 0) AS total_minutes" +
        "    FROM seance s" +
        "    INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id" +
        "    WHERE s.cours_id = :offreUeId" +
        "      AND et.classe_id = :classeId" +
        "      AND s.deleted = false" +
        "      AND et.deleted = false" +
        "      AND s.date_seance <= CURRENT_DATE" +
        " ) prog" +
        " WHERE sp.id = :etudiantId"
    )
    List<TeacherEtudiantCoursRow> findEtudiantStatsForCours(
            @Param("offreUeId")  Long offreUeId,
            @Param("classeId")   Long classeId,
            @Param("etudiantId") Long etudiantId
    );

    /**
     * Retourne les statistiques globales d'une paire (offreUe, classe).
     * Résultats : totalSeances, totalMinutesProgrammes, nbEtudiants,
     *             nbPresencesTotal, nbAbsencesTotal, tauxPresenceGlobal.
     */
    @Query(nativeQuery = true, value =
        "SELECT" +
        "    s_agg.total_seances            AS totalSeances," +
        "    s_agg.total_minutes            AS totalMinutesProgrammes," +
        "    classe_agg.nb_etudiants        AS nbEtudiants," +
        "    COALESCE(pr_agg.nb_presents, 0)   AS nbPresencesTotal," +
        "    COALESCE(pr_agg.nb_absents, 0)    AS nbAbsencesTotal," +
        "    COALESCE(ROUND(100.0 * COALESCE(pr_agg.nb_presents, 0)" +
        "        / NULLIF(COALESCE(pr_agg.nb_presents, 0) + COALESCE(pr_agg.nb_absents, 0), 0)), 0) AS tauxPresenceGlobal" +
        " FROM" +
        "    (SELECT COUNT(DISTINCT s.id) AS total_seances," +
        "            COALESCE(SUM(TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)), 0) AS total_minutes" +
        "     FROM seance s INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id" +
        "     WHERE s.cours_id = :offreUeId AND et.classe_id = :classeId" +
        "       AND s.deleted = false AND et.deleted = false AND s.date_seance <= CURRENT_DATE" +
        "    ) s_agg," +
        "    (SELECT COUNT(*) AS nb_etudiants FROM student_profile WHERE classe_id = :classeId) classe_agg," +
        "    (SELECT" +
        "         SUM(CASE WHEN pr.present = true OR pr.present IS NULL THEN 1 ELSE 0 END) AS nb_presents," +
        "         SUM(CASE WHEN pr.present = false THEN 1 ELSE 0 END) AS nb_absents" +
        "     FROM presence_row pr" +
        "     INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "     WHERE pl.ue_id = :offreUeId AND pl.classe_id = :classeId AND pl.deleted = false" +
        "    ) pr_agg"
    )
    List<Object[]> findCoursStatsRaw(
            @Param("offreUeId") Long offreUeId,
            @Param("classeId")  Long classeId
    );
}
