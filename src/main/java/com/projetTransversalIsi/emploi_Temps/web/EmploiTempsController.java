package com.projetTransversalIsi.emploi_Temps.web;

import com.projetTransversalIsi.emploi_Temps.application.dto.*;
import com.projetTransversalIsi.emploi_Temps.application.services.DefaultEmploiTempsService;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsConflictException;
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

    private final DefaultEmploiTempsService emploiTempsService;

    @PostMapping
    public ResponseEntity<?> createEmploiTemps(@Valid @RequestBody CreateEmploiTempsRequestDTO request) {
        try {
            log.info("Création d'un emploi du temps: {}", request.libelle());
            EmploiTempsResponseDTO response = emploiTempsService.createEmploiTemps(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmploiTempsConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
            @RequestParam(required = false) Long filiereId,
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer semaine,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Recherche d'emplois du temps - filière: {}, niveau: {}", filiereId, niveauId);

        LocalDate dateObj = null;
        if (date != null) {
            dateObj = LocalDate.parse(date);
        }

        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                filiereId, niveauId, dateObj, semaine, includeDeleted
        );

        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
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

    @GetMapping("/filiere/{filiereId}")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getByFiliere(
            @PathVariable Long filiereId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Emplois du temps de la filière ID: {}", filiereId);
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                filiereId, null, null, null, includeDeleted
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getByNiveau(
            @PathVariable Long niveauId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Emplois du temps du niveau ID: {}", niveauId);
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, niveauId, null, null, includeDeleted
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/semaine/{semaine}")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getBySemaine(
            @PathVariable Integer semaine,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Emplois du temps de la semaine: {}", semaine);
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, null, semaine, includeDeleted
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getActiveEmploisTemps() {
        log.info("Liste des emplois du temps actifs");
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, null, null, false
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<EmploiTempsResponseDTO>> getDeletedEmploisTemps() {
        log.info("Liste des emplois du temps supprimés");
        SearchEmploiTempsRequestDTO criteria = new SearchEmploiTempsRequestDTO(
                null, null, null, null, true
        );
        List<EmploiTempsResponseDTO> resultats = emploiTempsService.searchEmplois(criteria);
        resultats = resultats.stream()
                .filter(EmploiTempsResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }
}