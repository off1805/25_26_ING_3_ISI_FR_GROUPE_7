package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesEnseignantRequestDTO.PeriodeType;
import com.projetTransversalIsi.emploi_temps.application.service.SeanceService;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantAujourdhuiUseCase;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantParSemaineUseCase;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantUseCase;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.SeanceConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@Slf4j
public class SeanceController {

    private final SeanceService seanceService;

    // ── Use cases professeur ──────────────────────────────────────────────────
    private final GetSeancesEnseignantAujourdhuiUseCase seancesAujourdhuiUseCase;
    private final GetSeancesEnseignantParSemaineUseCase seancesSemaineUseCase;
    private final GetSeancesEnseignantUseCase           seancesEnseignantUseCase;

    // =========================================================================
    // CRUD existant (inchangé)
    // =========================================================================

    @PostMapping
    public ResponseEntity<?> createSeance(@Valid @RequestBody CreateSeanceRequestDTO request) {
        try {
            log.info("Création d'une séance: date {}", request.dateSeance());
            SeanceResponseDTO response = seanceService.createSeance(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SeanceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSeanceRequestDTO request) {

        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Modification de la séance ID: {}", id);
        SeanceResponseDTO response = seanceService.updateSeance(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeance(@PathVariable Long id) {
        log.info("Suppression de la séance ID: {}", id);
        seanceService.deleteSeance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeanceResponseDTO> getSeanceById(@PathVariable Long id) {
        log.info("Recherche de la séance ID: {}", id);
        SeanceResponseDTO response = seanceService.getSeanceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SeanceResponseDTO>> searchSeances(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long enseignantId,
            @RequestParam(required = false) Long coursId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Recherche de séances - date: {}, enseignant: {}", date, enseignantId);

        LocalDate dateObj = null;
        if (date != null) {
            dateObj = LocalDate.parse(date);
        }

        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(
                dateObj, enseignantId, coursId, includeDeleted
        );

        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesByEnseignant(
            @PathVariable Long enseignantId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Séances de l'enseignant ID: {}", enseignantId);
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(
                null, enseignantId, null, includeDeleted
        );
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesByCours(
            @PathVariable Long coursId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Séances du cours ID: {}", coursId);
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(
                null, null, coursId, includeDeleted
        );
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesByDate(
            @PathVariable String date,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Séances du date: {}", date);
        LocalDate dateObj = LocalDate.parse(date);
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(
                dateObj, null, null, includeDeleted
        );
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SeanceResponseDTO>> getActiveSeances() {
        log.info("Liste des séances actives");
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(null, null, null, false);
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<SeanceResponseDTO>> getDeletedSeances() {
        log.info("Liste des séances supprimées");
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(null, null, null, true);
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria).stream()
                .filter(SeanceResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }

    // =========================================================================
    // NOUVEAUX endpoints — use cases professeur
    // =========================================================================

    /**
     * UC-1 : Séances d'un professeur pour le jour même.
     *
     * <pre>GET /api/seances/enseignant/{enseignantId}/aujourd-hui</pre>
     *
     * @param enseignantId   identifiant du professeur
     * @param includeDeleted inclure les séances supprimées (false par défaut)
     */
    @GetMapping("/enseignant/{enseignantId}/aujourd-hui")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesAujourdhui(
            @PathVariable Long enseignantId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("UC-1 : séances aujourd'hui pour l'enseignant {}", enseignantId);

        List<SeanceResponseDTO> resultats = seancesAujourdhuiUseCase.execute(
                new GetSeancesAujourdhuiRequestDTO(enseignantId, includeDeleted)
        );
        return ResponseEntity.ok(resultats);
    }

    /**
     * UC-2 : Séances d'un professeur pour une semaine donnée.
     *
     * <pre>GET /api/seances/enseignant/{enseignantId}/semaine?dateDeReference=2025-03-31</pre>
     *
     * @param enseignantId    identifiant du professeur
     * @param dateDeReference n'importe quel jour de la semaine souhaitée (null → semaine courante)
     * @param includeDeleted  inclure les séances supprimées (false par défaut)
     */
    @GetMapping("/enseignant/{enseignantId}/semaine")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesSemaine(
            @PathVariable Long enseignantId,
            @RequestParam(required = false) String dateDeReference,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        LocalDate reference = dateDeReference != null
                ? LocalDate.parse(dateDeReference)
                : null;

        log.info("UC-2 : séances semaine pour l'enseignant {} (référence: {})",
                enseignantId, reference);

        List<SeanceResponseDTO> resultats = seancesSemaineUseCase.execute(
                new GetSeancesSemaineRequestDTO(enseignantId, reference, includeDeleted)
        );
        return ResponseEntity.ok(resultats);
    }

    /**
     * UC-3 (générique) : Séances d'un professeur pour un jour OU une semaine.
     *
     * <pre>
     * GET /api/seances/enseignant/{enseignantId}/periode?periode=JOUR
     * GET /api/seances/enseignant/{enseignantId}/periode?periode=SEMAINE&dateDeReference=2025-03-31
     * </pre>
     *
     * @param enseignantId    identifiant du professeur
     * @param periode         {@code JOUR} ou {@code SEMAINE}
     * @param dateDeReference date de référence (null → aujourd'hui / semaine courante)
     * @param includeDeleted  inclure les séances supprimées (false par défaut)
     */
    @GetMapping("/enseignant/{enseignantId}/periode")
    public ResponseEntity<List<SeanceResponseDTO>> getSeancesPeriode(
            @PathVariable Long enseignantId,
            @RequestParam PeriodeType periode,
            @RequestParam(required = false) String dateDeReference,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        LocalDate reference = dateDeReference != null
                ? LocalDate.parse(dateDeReference)
                : null;

        log.info("UC-3 (générique) : enseignant {} / période {} / référence {}",
                enseignantId, periode, reference);

        List<SeanceResponseDTO> resultats = seancesEnseignantUseCase.execute(
                new GetSeancesEnseignantRequestDTO(enseignantId, periode, reference, includeDeleted)
        );
        return ResponseEntity.ok(resultats);
    }
}
