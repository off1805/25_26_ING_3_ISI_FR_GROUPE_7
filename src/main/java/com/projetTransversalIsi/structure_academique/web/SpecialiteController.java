package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.service.SpecialiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialites")
@RequiredArgsConstructor
@Slf4j
public class SpecialiteController {

    private final SpecialiteService specialiteService;

    @PostMapping
    public ResponseEntity<SpecialiteResponseDTO> createSpecialite(
            @Valid @RequestBody CreateSpecialiteRequestDTO request) {
        log.info("Requête de création de spécialité : code={}", request.code());
        return ResponseEntity.status(HttpStatus.CREATED).body(specialiteService.createSpecialite(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialiteResponseDTO> getSpecialiteById(@PathVariable Long id) {
        log.info("Requête de récupération de la spécialité id={}", id);
        return ResponseEntity.ok(specialiteService.getSpecialiteById(id));
    }

    @GetMapping
    public ResponseEntity<List<SpecialiteResponseDTO>> getAllSpecialites() {
        log.info("Requête de récupération de toutes les spécialités actives");
        return ResponseEntity.ok(specialiteService.getAllSpecialites());
    }

    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<List<SpecialiteResponseDTO>> getSpecialitesByNiveau(
            @PathVariable Long niveauId) {
        log.info("Requête de spécialités pour le niveau={}", niveauId);
        return ResponseEntity.ok(specialiteService.getSpecialitesByNiveauId(niveauId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialiteResponseDTO> updateSpecialite(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSpecialiteRequestDTO request) {
        log.info("Requête de mise à jour de la spécialité id={}", id);
        return ResponseEntity.ok(specialiteService.updateSpecialite(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSpecialiteResponseDTO> deleteSpecialite(@PathVariable Long id) {
        log.info("Requête de suppression de la spécialité id={}", id);
        SpecialiteResponseDTO specialiteBeforeDeletion = specialiteService.getSpecialiteById(id);
        specialiteService.deleteSpecialite(id);
        
        // Convert to DeleteSpecialiteResponseDTO
        DeleteSpecialiteResponseDTO response = new DeleteSpecialiteResponseDTO(
                specialiteBeforeDeletion.id(),
                specialiteBeforeDeletion.code(),
                specialiteBeforeDeletion.libelle(),
                "Spécialité supprimée avec succès."
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<SpecialiteResponseDTO> activerSpecialite(@PathVariable Long id) {
        log.info("Requête d'activation de la spécialité id={}", id);
        specialiteService.toggleStatus(id, true);
        return ResponseEntity.ok(specialiteService.getSpecialiteById(id));
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<SpecialiteResponseDTO> desactiverSpecialite(@PathVariable Long id) {
        log.info("Requête de désactivation de la spécialité id={}", id);
        specialiteService.toggleStatus(id, false);
        return ResponseEntity.ok(specialiteService.getSpecialiteById(id));
    }
}
