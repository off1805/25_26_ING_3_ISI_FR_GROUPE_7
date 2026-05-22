package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataTeacherCoursRepository;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/cours")
@RequiredArgsConstructor
public class TeacherCoursApiController {

    private final SpringDataTeacherCoursRepository coursRepository;
    private final SpringDataUserRepository userRepository;

    /**
     * GET /api/teacher/cours
     * Retourne la liste des cartes (offreUe × classe) pour l'enseignant authentifié.
     */
    @GetMapping
    public ResponseEntity<List<TeacherCoursCardDTO>> getCoursCards(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long enseignantId = resolveEnseignantId(principal);
        List<TeacherCoursCardDTO> cards = coursRepository
                .findCoursCardsForEnseignant(enseignantId)
                .stream()
                .map(TeacherCoursCardDTO::from)
                .toList();
        return ResponseEntity.ok(cards);
    }

    /**
     * GET /api/teacher/cours/{offreUeId}/classe/{classeId}/stats
     * Statistiques générales d'un cours pour une classe.
     */
    @GetMapping("/{offreUeId}/classe/{classeId}/stats")
    public ResponseEntity<TeacherCoursStatsDTO> getCoursStats(
            @PathVariable Long offreUeId,
            @PathVariable Long classeId) {

        List<Object[]> rows = coursRepository.findCoursStatsRaw(offreUeId, classeId);
        if (rows.isEmpty()) {
            return ResponseEntity.ok(new TeacherCoursStatsDTO(0, 0, 0, 100, 0, 0));
        }
        Object[] r = rows.get(0);
        long totalSeances      = toLong(r[0]);
        long totalMinutes      = toLong(r[1]);
        long nbEtudiants       = toLong(r[2]);
        long nbPresents        = toLong(r[3]);
        long nbAbsents         = toLong(r[4]);
        int  tauxPresence      = (int) toLong(r[5]);

        return ResponseEntity.ok(new TeacherCoursStatsDTO(
                totalSeances, totalMinutes, nbEtudiants,
                tauxPresence, nbPresents, nbAbsents));
    }

    /**
     * GET /api/teacher/cours/{offreUeId}/classe/{classeId}/etudiants
     * Liste des étudiants avec leurs stats de présence pour ce cours.
     */
    @GetMapping("/{offreUeId}/classe/{classeId}/etudiants")
    public ResponseEntity<List<TeacherEtudiantCoursDTO>> getEtudiants(
            @PathVariable Long offreUeId,
            @PathVariable Long classeId) {

        List<TeacherEtudiantCoursDTO> etudiants = coursRepository
                .findEtudiantsStatsForCours(offreUeId, classeId)
                .stream()
                .map(TeacherEtudiantCoursDTO::from)
                .toList();
        return ResponseEntity.ok(etudiants);
    }

    /**
     * GET /api/teacher/cours/{offreUeId}/classe/{classeId}/etudiants/{etudiantId}
     * Stats de présence d'un étudiant précis pour ce cours.
     */
    @GetMapping("/{offreUeId}/classe/{classeId}/etudiants/{etudiantId}")
    public ResponseEntity<TeacherEtudiantCoursDTO> getEtudiantStats(
            @PathVariable Long offreUeId,
            @PathVariable Long classeId,
            @PathVariable Long etudiantId) {

        List<TeacherEtudiantCoursDTO> result = coursRepository
                .findEtudiantStatsForCours(offreUeId, classeId, etudiantId)
                .stream()
                .map(TeacherEtudiantCoursDTO::from)
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get(0));
    }

    private Long resolveEnseignantId(UserPrincipal principal) {
        if (principal == null) return -1L;
        return userRepository.findById(principal.userId())
                .map(u -> u.getProfile() != null ? u.getProfile().getId() : -1L)
                .orElse(-1L);
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        return 0L;
    }
}
