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
        " COUNT(DISTINCT pr.id) AS totalEnregistrements," +
        " CAST(COALESCE(SUM(CASE WHEN (pr.present IS NULL OR pr.present = false) THEN 1 ELSE 0 END), 0) AS SIGNED) AS totalAbsences," +
        " CAST(COALESCE((SELECT COUNT(j.id) FROM justificatif j WHERE j.etudiant_id = sp.id AND j.statut = 'APPROVED'), 0) AS SIGNED) AS nbJustifiees," +
        " CAST(COALESCE((SELECT COUNT(j.id) FROM justificatif j WHERE j.etudiant_id = sp.id AND j.statut = 'PENDING'), 0) AS SIGNED) AS nbEnAttente" +
        " FROM student_profile sp" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " INNER JOIN classe c ON c.id = sp.classe_id" +
        " LEFT JOIN presence_row pr ON pr.etudiant_id = sp.id" +
        " LEFT JOIN presence_list pl ON pl.id = pr.presence_list_id" +
        "   AND pl.deleted = false" +
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
        " ORDER BY totalAbsences DESC, p.nom"
    )
    List<AbsenceEtudiantRow> findAbsencesParEtudiant(
            @Param("filiereId") Long filiereId,
            @Param("classeId") Long classeId,
            @Param("ueId") Long ueId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query(nativeQuery = true, value =
        "SELECT DISTINCT pl.ue_id AS id, ue.libelle AS libelle, ue.code AS code" +
        " FROM presence_list pl" +
        " INNER JOIN offre_ue ue ON ue.id = pl.ue_id" +
        " WHERE pl.deleted = false" +
        " AND pl.classe_id IN (" +
        "   SELECT c.id FROM classe c" +
        "   INNER JOIN specialite s ON s.id = c.specialite_id" +
        "   INNER JOIN niveau n ON n.id = s.niveau_id" +
        "   WHERE n.filiere_id = :filiereId" +
        " )" +
        " ORDER BY ue.libelle"
    )
    List<UeMetaRow> findUesWithPresenceData(@Param("filiereId") Long filiereId);
}
