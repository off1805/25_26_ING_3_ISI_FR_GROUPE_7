package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.application.service.EmploiTempsService;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.EmploiTempsConflictException;
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
@RequestMapping("/api/emplois-temps")
@RequiredArgsConstructor
@Slf4j
public class EmploiTempsController {

    private final EmploiTempsService emploiTempsService;

    @PostMapping
    public ResponseEntity<?> createEmploiTemps(@Valid @RequestBody CreateEmploiTempsRequestDTO request) {
        try {
            log.info("Création d'un emploi du temps pour classe {}", request.classeId());
            EmploiTempsResponseDTO response = emploiTempsService.createEmploiTemps(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmploiTempsConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/with-seances")
    public ResponseEntity<?> createEmploiTempsWithSeances(
            @Valid @RequestBody CreateEmploiTempsWithSeancesDTO request) {
        try {
            log.info("Création d'un emploi du temps avec {} séances pour la classe {}",
                    request.seances().size(), request.classeId());
            EmploiTempsResponseDTO response = emploiTempsService.createEmploiTempsWithSeances(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmploiTempsConflictException | SeanceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/with-seances")
    public ResponseEntity<?> updateEmploiTempsWithSeances(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmploiTempsWithSeancesDTO request) {
        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            log.info("Mise à jour de l'emploi du temps {} avec {} séances", id, request.seances().size());
            EmploiTempsResponseDTO response = emploiTempsService.updateEmploiTempsWithSeances(request);
            return ResponseEntity.ok(response);
        } catch (EmploiTempsConflictException | SeanceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmploiTemps(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmploiTempsRequestDTO request) {

        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Modification de l'emploi du temps ID: {}", id);
        EmploiTempsResponseDTO response = emploiTempsService.updateEmploiTemps(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmploiTemps(@PathVariable Long id) {
        log.info("Suppression de l'emploi du temps ID: {}", id);
        emploiTempsService.deleteEmploiTemps(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmploiTempsResponseDTO> getEmploiTempsById(@PathVariable Long id) {
        log.info("Recherche de l'emploi du temps ID: {}", id);
        EmploiTempsResponseDTO response = emploiTempsService.getEmploiTempsById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmploiTempsResponseDTO>> searchEmploisTemps(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer semaine,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Recherche d'emplois du temps - classe: {}", classeId);

        LocalDate dateObj = null;
        if (date != null) {
            dateObj = LocalDate.parse(date);
        }

        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                classeId, dateObj, semaine, includeDeleted
        );

        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmploiTemps(criteria);
        return ResponseEntity.ok(resultats);
    }

    @PostMapping("/seances")
    public ResponseEntity<EmploiTempsResponseDTO> addSeanceToEmploiTemps(
            @Valid @RequestBody AddSeanceToEmploiDTO request) {
        log.info("Ajout de la séance {} à l'emploi du temps {}",
                request.seanceId(), request.emploiTempsId());
        EmploiTempsResponseDTO response = emploiTempsService.addSeanceToEmploi(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/seances")
    public ResponseEntity<EmploiTempsResponseDTO> removeSeanceFromEmploiTemps(
            @Valid @RequestBody AddSeanceToEmploiDTO request) {
        log.info("Retrait de la séance {} de l'emploi du temps {}",
                request.seanceId(), request.emploiTempsId());
        EmploiTempsResponseDTO response = emploiTempsService.removeSeanceFromEmploi(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getByClasse(
            @PathVariable Long classeId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Emplois du temps de la classe ID: {}", classeId);
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                classeId, null, null, includeDeleted
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmploiTemps(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/semaine/{semaine}")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getBySemaine(
            @PathVariable Integer semaine,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Emplois du temps de la semaine: {}", semaine);
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, semaine, includeDeleted
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmploiTemps(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getActiveEmploisTemps() {
        log.info("Liste des emplois du temps actifs");
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, null, false
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmploiTemps(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getDeletedEmploisTemps() {
        log.info("Liste des emplois du temps supprimés");
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, null, true
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmploiTemps(criteria).stream()
                .filter(EmploiTempsResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }
}
