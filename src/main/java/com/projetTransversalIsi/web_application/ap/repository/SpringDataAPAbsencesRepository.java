package com.projetTransversalIsi.web_application.ap.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringDataAPAbsencesRepository extends JpaRepository<JpaPresenceRowEntity, Long> {

    @Query(nativeQuery = true, value =
        "SELECT sp.id AS etudiantId, p.nom AS nom, p.prenom AS prenom," +
        " p.matricule AS matricule, p.photo_url AS photoUrl," +
        " sp.classe_id AS classeId, c.code AS classeCode," +
        " (SELECT COUNT(DISTINCT pl2.id) FROM presence_list pl2" +
        "  WHERE pl2.deleted = false AND pl2.classe_id = sp.classe_id" +
        "  AND pl2.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        "  AND (:ueId IS NULL OR pl2.ue_id = :ueId)" +
        "  AND (:dateDebut IS NULL OR pl2.date >= :dateDebut)" +
        "  AND (:dateFin IS NULL OR pl2.date <= :dateFin)) AS totalSeances," +
        " CAST(COALESCE(SUM(CASE WHEN pl.id IS NOT NULL AND (pr.present IS NULL OR pr.present = false) THEN 1 ELSE 0 END), 0) AS SIGNED) AS totalAbsences," +
        " CAST(COALESCE((SELECT COUNT(j.id) FROM justificatif j WHERE j.etudiant_id = sp.id AND j.statut = 'APPROVED'" +
        "   AND j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)), 0) AS SIGNED) AS nbJustifiees," +
        " CAST(COALESCE((SELECT COUNT(j.id) FROM justificatif j WHERE j.etudiant_id = sp.id AND j.statut = 'PENDING'" +
        "   AND j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)), 0) AS SIGNED) AS nbEnAttente" +
        " FROM student_profile sp" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " INNER JOIN classe c ON c.id = sp.classe_id" +
        " LEFT JOIN presence_row pr ON pr.etudiant_id = sp.id" +
        " LEFT JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "   AND pl.deleted = false" +
        "   AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        "   AND (:ueId IS NULL OR pl.ue_id = :ueId)" +
        "   AND (:dateDebut IS NULL OR pl.date >= :dateDebut)" +
        "   AND (:dateFin IS NULL OR pl.date <= :dateFin)" +
        " WHERE sp.classe_id IN (" +
        "   SELECT c2.id FROM classe c2" +
        "   INNER JOIN specialite s ON s.id = c2.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " AND (:classeId IS NULL OR sp.classe_id = :classeId)" +
        " GROUP BY sp.id, p.nom, p.prenom, p.matricule, p.photo_url, sp.classe_id, c.code" +
        " HAVING totalAbsences > 0" +
        " ORDER BY totalAbsences DESC, p.nom"
    )
    List<AbsenceEtudiantRow> findAbsencesParEtudiant(
            @Param("filiereId") Long filiereId,
            @Param("classeId") Long classeId,
            @Param("ueId") Long ueId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // ── Export Excel ──────────────────────────────────────────────────────────

    @Query(nativeQuery = true, value =
        "SELECT sp.id AS etudiantId, p.nom AS nom, p.prenom AS prenom," +
        " p.matricule AS matricule, c.code AS classeCode" +
        " FROM student_profile sp" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " INNER JOIN classe c ON c.id = sp.classe_id" +
        " WHERE sp.classe_id = :classeId" +
        " AND EXISTS (" +
        "   SELECT 1 FROM classe c2" +
        "   INNER JOIN specialite s ON s.id = c2.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE c2.id = :classeId AND n.filiere_id = :filiereId" +
        " )" +
        " ORDER BY p.nom, p.prenom"
    )
    List<ExportStudentRow> findStudentsInClasseForExport(
            @Param("classeId") Long classeId,
            @Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT DISTINCT pl.ue_id AS ueId, ou.libelle AS libelle," +
        " ou.code AS code, COALESCE(ou.volume_horaire_total, 0) AS volumeHoraireTotal" +
        " FROM presence_list pl" +
        " INNER JOIN offre_ue ou ON ou.id = pl.ue_id" +
        " WHERE pl.deleted = false AND pl.classe_id = :classeId" +
        " AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " ORDER BY ou.libelle"
    )
    List<ExportUeInfoRow> findUesForExport(@Param("classeId") Long classeId);

    @Query(nativeQuery = true, value =
        // Même logique que SpringDataPresenceStatsRepository :
        // si info_presence_row existe → SUM des minutes is_present=false
        // sinon → durée totale de la séance (étudiant absent toute la séance)
        "SELECT sp.id AS etudiantId," +
        " pl.ue_id AS ueId," +
        " COALESCE(SUM(" +
        "   CASE WHEN COALESCE(ipr_count.cnt, 0) > 0" +
        "        THEN COALESCE(ipr_abs.abs_minutes, 0)" +
        "        ELSE TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)" +
        "   END" +
        " ), 0) / 60.0 AS heuresAbsence," +
        " COALESCE(SUM(" +
        "   CASE WHEN COALESCE(jus_check.has_approved, 0) = 1 THEN" +
        "     CASE WHEN COALESCE(ipr_count.cnt, 0) > 0" +
        "          THEN COALESCE(ipr_abs.abs_minutes, 0)" +
        "          ELSE TIMESTAMPDIFF(MINUTE, s.heure_debut, s.heure_fin)" +
        "     END" +
        "   ELSE 0 END" +
        " ), 0) / 60.0 AS heuresJustifiees" +
        " FROM student_profile sp" +
        " INNER JOIN presence_row pr ON pr.etudiant_id = sp.id" +
        " INNER JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "   AND pl.deleted = false AND pl.classe_id = :classeId" +
        "   AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " INNER JOIN seance s ON s.id = pl.seance_id AND s.deleted = false" +
        " LEFT JOIN (" +
        "   SELECT presence_row_id, COUNT(*) AS cnt" +
        "   FROM info_presence_row" +
        "   GROUP BY presence_row_id" +
        " ) ipr_count ON ipr_count.presence_row_id = pr.id" +
        " LEFT JOIN (" +
        "   SELECT presence_row_id," +
        "          SUM(TIMESTAMPDIFF(MINUTE, heure_debut, heure_fin)) AS abs_minutes" +
        "   FROM info_presence_row" +
        "   WHERE is_present = false" +
        "   GROUP BY presence_row_id" +
        " ) ipr_abs ON ipr_abs.presence_row_id = pr.id" +
        " LEFT JOIN (" +
        "   SELECT js.seance_id, j.etudiant_id," +
        "          MAX(CASE WHEN j.statut = 'APPROVED' THEN 1 ELSE 0 END) AS has_approved" +
        "   FROM justificatif_seance js" +
        "   INNER JOIN justificatif j ON j.id = js.justificatif_id" +
        "   WHERE j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        "   GROUP BY js.seance_id, j.etudiant_id" +
        " ) jus_check ON jus_check.seance_id = pl.seance_id AND jus_check.etudiant_id = sp.id" +
        " WHERE sp.classe_id = :classeId" +
        "   AND (pr.present IS NULL OR pr.present = false)" +
        " GROUP BY sp.id, pl.ue_id"
    )
    List<ExportAbsenceUeRow> findAbsencesParUeForExport(@Param("classeId") Long classeId);

    @Query(nativeQuery = true, value =
        "SELECT DISTINCT pl.ue_id AS id, ue.libelle AS libelle, ue.code AS code" +
        " FROM presence_list pl" +
        " INNER JOIN offre_ue ue ON ue.id = pl.ue_id" +
        " WHERE pl.deleted = false" +
        " AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND pl.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite s ON s.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " ORDER BY ue.libelle"
    )
    List<UeMetaRow> findUesWithPresenceData(@Param("filiereId") Long filiereId);

    @Query(nativeQuery = true, value =
        "SELECT s.id AS seanceId," +
        " s.date_seance AS date," +
        " s.heure_debut AS heureDebut," +
        " s.heure_fin AS heureFin," +
        " s.salle AS salle," +
        " ue.libelle AS ueLibelle," +
        " ue.code AS ueCode," +
        " COALESCE(pr.present, false) AS present," +
        " (SELECT j.statut FROM justificatif j" +
        "   INNER JOIN justificatif_seance js ON js.justificatif_id = j.id" +
        "   WHERE j.etudiant_id = :etudiantId AND js.seance_id = s.id" +
        "   AND j.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        "   ORDER BY j.created_at DESC LIMIT 1) AS justificatifStatut" +
        " FROM presence_row pr" +
        " INNER JOIN presence_list pl ON pl.id = pr.presence_list_id AND pl.deleted = false" +
        "   AND pl.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " INNER JOIN seance s ON s.id = pl.seance_id AND s.deleted = false" +
        " INNER JOIN offre_ue ue ON ue.id = pl.ue_id" +
        " WHERE pr.etudiant_id = :etudiantId" +
        " AND pl.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite sp ON sp.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = sp.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " ORDER BY s.date_seance DESC, s.heure_debut DESC"
    )
    List<AbsenceDetailLigneRow> findDetailAbsencesEtudiant(
            @Param("filiereId") Long filiereId,
            @Param("etudiantId") Long etudiantId);
}
