package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.service.FiliereService;
import com.projetTransversalIsi.structure_academique.application.dto.SearchFiliereRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/filiere")
@RequiredArgsConstructor
@Slf4j
public class FiliereController {

    private final FiliereService filiereService;

    @PostMapping
    public ResponseEntity<FiliereResponseDTO> createFiliere(
            @Valid @RequestBody CreateFiliereRequestDTO request) {
        log.info("Création d'une filière: {}", request.code());
        return ResponseEntity.status(HttpStatus.CREATED).body(filiereService.createFiliere(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FiliereResponseDTO> updateFiliere(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFiliereRequestDTO request) {
        log.info("Modification de la filière ID: {}", id);
        return ResponseEntity.ok(filiereService.updateFiliere(id, request));
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
        return ResponseEntity.ok(filiereService.getFiliereById(id));
    }

    @GetMapping
    public ResponseEntity<List<FiliereResponseDTO>> searchFilieres(
            @ModelAttribute SearchFiliereRequestDTO command) {
        return ResponseEntity.ok(filiereService.searchFilieres(command));
    }

    @GetMapping("/active")
    public ResponseEntity<List<FiliereResponseDTO>> getAllActiveFilieres() {
        log.info("Liste de toutes les filières actives");
        SearchFiliereRequestDTO criteria = new SearchFiliereRequestDTO(null, null, null, false);
        return ResponseEntity.ok(filiereService.searchFilieres(criteria));
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<FiliereResponseDTO>> getDeletedFilieres() {
        log.info("Liste des filières supprimées");
        SearchFiliereRequestDTO criteria = new SearchFiliereRequestDTO(null, null, null, true);
        List<FiliereResponseDTO> resultats = filiereService.searchFilieres(criteria).stream()
                .filter(FiliereResponseDTO::deleted)
                .toList();
        return ResponseEntity.ok(resultats);
    }
}
