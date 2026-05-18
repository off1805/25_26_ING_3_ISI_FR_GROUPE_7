package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.application.dto.MatiereStatsRow;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataPresenceStatsRepository extends JpaRepository<JpaPresenceRowEntity, Long> {

    /**
     * Retourne les statistiques de présence agrégées par unité d'enseignement
     * pour un étudiant donné dans sa classe.
     *
     * Calculs SQL :
     *  - totalMinutesProgrammes : somme des durées des séances passées de la classe
     *    liées à chaque offre_ue (seance.cours_id = offre_ue.id).
     *  - minutesAbsence : depuis info_presence_row (granularité appel) si disponible,
     *    sinon durée totale de la séance.
     *  - breakdown justificatifs : priorité APPROVED > PENDING > REJECTED > aucun.
     */
    @Query(nativeQuery = true, value =
        "SELECT" +
        "    ou.id                                AS offreUeId," +
        "    ou.libelle                           AS libelle," +
        "    ou.code                              AS code," +
        "    ou.credit                            AS credit," +
        "    ou.volume_horaire_total              AS volumeHoraireTotal," +
        "    ou.semestre                          AS semestre," +
        "    ou.couleur                           AS couleur," +
        "    ou.description                       AS description," +
        "    COALESCE(prog.total_minutes, 0)      AS totalMinutesProgrammes," +
        "    COALESCE(abs_s.minutes_absence, 0)   AS minutesAbsence," +
        "    COALESCE(abs_s.nb_absences, 0)       AS nbAbsences," +
        "    COALESCE(abs_s.nb_non_justifiees, 0) AS nbNonJustifiees," +
        "    COALESCE(abs_s.nb_en_cours, 0)       AS nbEnCours," +
        "    COALESCE(abs_s.nb_justifiees, 0)     AS nbJustifiees," +
        "    COALESCE(abs_s.nb_rejetees, 0)       AS nbRejetees" +
        " FROM offre_ue ou" +
        " LEFT JOIN (" +
        "    SELECT s.cours_id AS offre_ue_id," +
        "           SUM(TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)) AS total_minutes" +
        "    FROM seance s" +
        "    INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id" +
        "    WHERE et.classe_id = :classeId" +
        "      AND et.deleted = false" +
        "      AND s.deleted = false" +
        "      AND s.type = 'SEANCE'" +
        "      AND s.cours_id IS NOT NULL" +
        "      AND s.date_seance <= CURRENT_DATE" +
        "    GROUP BY s.cours_id" +
        " ) prog ON prog.offre_ue_id = ou.id" +
        " LEFT JOIN (" +
        "    SELECT pl.ue_id AS offre_ue_id," +
        "           SUM(CASE" +
        "               WHEN COALESCE(ipr_count.cnt, 0) > 0" +
        "               THEN COALESCE(ipr_abs.abs_minutes, 0)" +
        "               ELSE TIMESTAMPDIFF(MINUTE, s2.heure_debut, s2.heure_fin)" +
        "           END) AS minutes_absence," +
        "           COUNT(DISTINCT pr.id) AS nb_absences," +
        "           COUNT(DISTINCT CASE WHEN COALESCE(jus.has_approved,0)=1 THEN pr.id END) AS nb_justifiees," +
        "           COUNT(DISTINCT CASE WHEN COALESCE(jus.has_approved,0)=0 AND COALESCE(jus.has_pending,0)=1 THEN pr.id END) AS nb_en_cours," +
        "           COUNT(DISTINCT CASE WHEN COALESCE(jus.has_approved,0)=0 AND COALESCE(jus.has_pending,0)=0 AND COALESCE(jus.has_rejected,0)=1 THEN pr.id END) AS nb_rejetees," +
        "           COUNT(DISTINCT CASE WHEN jus.seance_id IS NULL THEN pr.id END) AS nb_non_justifiees" +
        "    FROM presence_row pr" +
        "    INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "    INNER JOIN seance s2 ON s2.id = pl.seance_id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id, COUNT(*) AS cnt" +
        "        FROM info_presence_row" +
        "        GROUP BY presence_row_id" +
        "    ) ipr_count ON ipr_count.presence_row_id = pr.id" +
        "    LEFT JOIN (" +
        "        SELECT presence_row_id," +
        "               SUM(TIMESTAMPDIFF(MINUTE, heure_debut, heure_fin)) AS abs_minutes" +
        "        FROM info_presence_row" +
        "        WHERE is_present = false" +
        "        GROUP BY presence_row_id" +
        "    ) ipr_abs ON ipr_abs.presence_row_id = pr.id" +
        "    LEFT JOIN (" +
        "        SELECT js.seance_id," +
        "               MAX(CASE WHEN j.statut = 'APPROVED' THEN 1 ELSE 0 END) AS has_approved," +
        "               MAX(CASE WHEN j.statut = 'PENDING'  THEN 1 ELSE 0 END) AS has_pending," +
        "               MAX(CASE WHEN j.statut = 'REJECTED' THEN 1 ELSE 0 END) AS has_rejected" +
        "        FROM justificatif_seance js" +
        "        INNER JOIN justificatif j ON j.id = js.justificatif_id" +
        "        WHERE j.etudiant_id = :etudiantId" +
        "        GROUP BY js.seance_id" +
        "    ) jus ON jus.seance_id = pl.seance_id" +
        "    WHERE pr.etudiant_id = :etudiantId" +
        "      AND (pr.present IS NULL OR pr.present = false)" +
        "      AND pl.deleted = false" +
        "    GROUP BY pl.ue_id" +
        " ) abs_s ON abs_s.offre_ue_id = ou.id" +
        " WHERE ou.specialite_id = :specialiteId" +
        "   AND ou.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true LIMIT 1)" +
        " ORDER BY ou.semestre, ou.libelle"
    )
    List<MatiereStatsRow> findMatiereStatsEtudiant(
            @Param("etudiantId")  Long etudiantId,
            @Param("classeId")    Long classeId,
            @Param("specialiteId") Long specialiteId
    );
}
