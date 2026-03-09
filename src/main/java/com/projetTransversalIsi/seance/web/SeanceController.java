package com.projetTransversalIsi.seance.web;

import com.projetTransversalIsi.seance.application.dto.*;
import com.projetTransversalIsi.seance.application.services.DefaultSeanceService;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceConflictException;
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

    private final DefaultSeanceService seanceService;

    @PostMapping
    public ResponseEntity<?> createSeance(@Valid @RequestBody CreateSeanceRequestDTO request) {
        try {
            log.info("Création d'une séance: {} - {}", request.libelle(), request.dateSeance());
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
            @RequestParam(required = false) String salle,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Recherche de séances - date: {}, enseignant: {}", date, enseignantId);

        LocalDate dateObj = null;
        if (date != null) {
            dateObj = LocalDate.parse(date);
        }

        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(
                dateObj, enseignantId, coursId, salle, includeDeleted
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
                null, enseignantId, null, null, includeDeleted
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
                null, null, coursId, null, includeDeleted
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
                dateObj, null, null, null, includeDeleted
        );
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SeanceResponseDTO>> getActiveSeances() {
        log.info("Liste des séances actives");
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(null, null, null, null, false);
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<SeanceResponseDTO>> getDeletedSeances() {
        log.info("Liste des séances supprimées");
        SearchSeanceRequestDTO criteria = new SearchSeanceRequestDTO(null, null, null, null, true);
        List<SeanceResponseDTO> resultats = seanceService.searchSeances(criteria);
        resultats = resultats.stream()
                .filter(SeanceResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }
}