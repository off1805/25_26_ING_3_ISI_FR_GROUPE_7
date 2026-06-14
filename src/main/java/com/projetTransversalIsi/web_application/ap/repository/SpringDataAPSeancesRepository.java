package com.projetTransversalIsi.web_application.ap.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaSeanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataAPSeancesRepository extends JpaRepository<JpaSeanceEntity, Long> {

    String SEANCE_ROW_SELECT =
        "SELECT s.id AS seanceId, s.libelle AS libelle, s.salle AS salle," +
        " s.date_seance AS dateSeance, s.heure_debut AS heureDebut, s.heure_fin AS heureFin," +
        " et.classe_id AS classeId, c.code AS classeCode," +
        " pe.nom AS enseignantNom, pe.prenom AS enseignantPrenom," +
        " (SELECT pl2.id FROM presence_list pl2 WHERE pl2.seance_id = s.id AND pl2.deleted = false ORDER BY pl2.id LIMIT 1) AS presenceListId," +
        " CAST(COALESCE((SELECT COUNT(*) FROM presence_row pr INNER JOIN presence_list pl3 ON pl3.id = pr.presence_list_id" +
        "   WHERE pl3.seance_id = s.id AND pl3.deleted = false AND pr.present = true), 0) AS SIGNED) AS nbPresents," +
        " CAST(COALESCE((SELECT COUNT(*) FROM presence_row pr INNER JOIN presence_list pl4 ON pl4.id = pr.presence_list_id" +
        "   WHERE pl4.seance_id = s.id AND pl4.deleted = false AND (pr.present = false OR pr.present IS NULL)), 0) AS SIGNED) AS nbAbsents," +
        " CAST(COALESCE((SELECT COUNT(*) FROM presence_row pr INNER JOIN presence_list pl5 ON pl5.id = pr.presence_list_id" +
        "   WHERE pl5.seance_id = s.id AND pl5.deleted = false), 0) AS SIGNED) AS nbTotal" +
        " FROM seance s" +
        " INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id AND et.deleted = false" +
        " INNER JOIN classe c ON c.id = et.classe_id" +
        " LEFT JOIN profile pe ON pe.id = s.enseignant_id";

    String FILIERE_CLASSES_CONDITION =
        " et.classe_id IN (" +
        "   SELECT c2.id FROM classe c2" +
        "   INNER JOIN specialite sp2 ON sp2.id = c2.specialite_id" +
        "   INNER JOIN niveau n2 ON n2.id = sp2.niveau_id" +
        "   WHERE n2.filiere_id = :filiereId" +
        " )";

    @Query(value = SEANCE_ROW_SELECT +
        " WHERE s.deleted = false AND s.type = 'SEANCE'" +
        " AND s.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND " + FILIERE_CLASSES_CONDITION +
        " AND s.date_seance BETWEEN :dateDebut AND :dateFin" +
        " ORDER BY s.date_seance ASC, s.heure_debut ASC",
        nativeQuery = true)
    List<SeanceApRow> findSeancesSemaine(
            @Param("filiereId") Long filiereId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query(value = SEANCE_ROW_SELECT +
        " WHERE s.deleted = false AND s.type = 'SEANCE'" +
        " AND s.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND " + FILIERE_CLASSES_CONDITION +
        " AND s.date_seance < :avantDate" +
        " AND (:classeId IS NULL OR et.classe_id = :classeId)" +
        " ORDER BY s.date_seance DESC, s.heure_debut DESC",
        countQuery =
        "SELECT COUNT(*) FROM seance s" +
        " INNER JOIN emploi_temps et ON et.id = s.emploi_temps_id AND et.deleted = false" +
        " WHERE s.deleted = false AND s.type = 'SEANCE'" +
        " AND s.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND " + FILIERE_CLASSES_CONDITION +
        " AND s.date_seance < :avantDate" +
        " AND (:classeId IS NULL OR et.classe_id = :classeId)",
        nativeQuery = true)
    Page<SeanceApRow> findSeancesHistorique(
            @Param("filiereId") Long filiereId,
            @Param("avantDate") LocalDate avantDate,
            @Param("classeId") Long classeId,
            Pageable pageable);

    @Query(value = SEANCE_ROW_SELECT +
        " WHERE s.id = :seanceId AND s.deleted = false" +
        " AND s.annee_scolaire_id = (SELECT id FROM annee_scolaire WHERE active = true)" +
        " AND " + FILIERE_CLASSES_CONDITION,
        nativeQuery = true)
    Optional<SeanceApRow> findSeanceById(
            @Param("seanceId") Long seanceId,
            @Param("filiereId") Long filiereId);

    @Query(value =
        "SELECT sp.id AS etudiantId, p.nom AS nom, p.prenom AS prenom, p.matricule AS matricule, pr.present AS present" +
        " FROM presence_row pr" +
        " INNER JOIN student_profile sp ON sp.id = pr.etudiant_id" +
        " INNER JOIN profile p ON p.id = sp.id" +
        " WHERE pr.presence_list_id = :presenceListId" +
        " ORDER BY p.nom, p.prenom",
        nativeQuery = true)
    List<SeancePresenceEtudiantRow> findPresenceEtudiants(@Param("presenceListId") Long presenceListId);
}
