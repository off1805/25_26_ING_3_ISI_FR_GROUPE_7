package com.projetTransversalIsi.Filiere.web;

import com.projetTransversalIsi.Filiere.application.dto.*;
import com.projetTransversalIsi.Filiere.application.services.DefaultFiliereService;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/filieres")
@RequiredArgsConstructor
@Slf4j
public class FiliereController {

    private final DefaultFiliereService filiereService;
    private final FiliereRepository filiereRepo;

    @PostMapping
    public ResponseEntity<FiliereResponseDTO> createFiliere(
            @Valid @RequestBody CreateFiliereRequestDTO request) {
        log.info("Création d'une filière: {}", request.code());
        FiliereResponseDTO response = filiereService.createFiliere(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<FiliereResponseDTO> updateFiliere(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFiliereRequestDTO request) {


        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Modification de la filière ID: {}", id);
        FiliereResponseDTO response = filiereService.updateFiliere(request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        log.info("Suppression de la filière ID: {}", id);
        filiereService.deleteFiliere(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<FiliereResponseDTO> getFiliereById(@PathVariable Long id) {
        log.info("Recherche de la filière ID: {}", id);
        FiliereResponseDTO response = filiereService.getFiliereById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<FiliereResponseDTO> getFiliereByCode(@PathVariable String code) {
        log.info("Recherche de la filière par code: {}", code);

        Filiere filiere = filiereRepo.findByCode(code)
                .orElseThrow(() -> new FiliereNotFoundException(code));

        return ResponseEntity.ok(FiliereResponseDTO.fromDomain(filiere));
    }


    @GetMapping
    public ResponseEntity<List<FiliereResponseDTO>> searchFilieres(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String nom,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        log.info("Recherche de filières - code: {}, nom: {}", code, nom);
        SearchFiliereRequestDTO criteria = new SearchFiliereRequestDTO(code, nom, includeDeleted);
        List<FiliereResponseDTO> resultats = filiereService.searchFilieres(criteria);
        return ResponseEntity.ok(resultats);
    }


    @GetMapping("/active")
    public ResponseEntity<List<FiliereResponseDTO>> getAllActiveFilieres() {
        log.info("Liste de toutes les filières actives");
        SearchFiliereRequestDTO criteria = new SearchFiliereRequestDTO(null, null, false);
        List<FiliereResponseDTO> resultats = filiereService.searchFilieres(criteria);
        return ResponseEntity.ok(resultats);
    }


    @GetMapping("/deleted")
    public ResponseEntity<List<FiliereResponseDTO>> getDeletedFilieres() {
        log.info("Liste des filières supprimées");
        SearchFiliereRequestDTO criteria = new SearchFiliereRequestDTO(null, null, true);
        List<FiliereResponseDTO> resultats = filiereService.searchFilieres(criteria);

        resultats = resultats.stream()
                .filter(FiliereResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }
}
