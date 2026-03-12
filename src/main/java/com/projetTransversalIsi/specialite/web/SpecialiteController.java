package com.projetTransversalIsi.specialite.web;

import com.projetTransversalIsi.specialite.application.dto.CreateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.application.dto.DeleteSpecialiteResponseDTO;
import com.projetTransversalIsi.specialite.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.specialite.application.dto.UpdateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.application.use_cases.*;
import com.projetTransversalIsi.specialite.domain.Specialite;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/specialites")
@RequiredArgsConstructor
@Slf4j
public class SpecialiteController {

    private final CreateSpecialiteUC createSpecialiteUC;
    private final FindSpecialiteByIdUC findSpecialiteByIdUC;
    private final GetAllSpecialitesUC getAllSpecialitesUC;
    private final GetSpecialitesByNiveauUC getSpecialitesByNiveauUC;
    private final UpdateSpecialiteUC updateSpecialiteUC;
    private final DeleteSpecialiteUC deleteSpecialiteUC;
    private final ToggleSpecialiteStatusUC toggleSpecialiteStatusUC;

    /**
     * POST /api/specialites
     * J'ai testé
     */
    @PostMapping
    public ResponseEntity<SpecialiteResponseDTO> createSpecialite(
            @Valid @RequestBody CreateSpecialiteRequestDTO request) {
        log.info("Requête de création de spécialité : code={}", request.code());
        Specialite specialite = createSpecialiteUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SpecialiteResponseDTO.fromDomain(specialite));
    }

    /**
     * GET /api/specialites/{id}
     * Récupère une spécialité par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpecialiteResponseDTO> getSpecialiteById(@PathVariable Long id) {
        log.info("Requête de récupération de la spécialité id={}", id);
        Specialite specialite = findSpecialiteByIdUC.execute(id);
        return ResponseEntity.ok(SpecialiteResponseDTO.fromDomain(specialite));
    }

    /**
     * GET /api/specialites
     * Récupère toutes les spécialités actives.
     */
    @GetMapping
    public ResponseEntity<List<SpecialiteResponseDTO>> getAllSpecialites() {
        log.info("Requête de récupération de toutes les spécialités actives");
        List<SpecialiteResponseDTO> result = getAllSpecialitesUC.execute().stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/specialites/niveau/{niveauId}
     * Récupère les spécialités d'un niveau donné.
     */
    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<List<SpecialiteResponseDTO>> getSpecialitesByNiveau(
            @PathVariable Long niveauId) {
        log.info("Requête de spécialités pour le niveau={}", niveauId);
        List<SpecialiteResponseDTO> result = getSpecialitesByNiveauUC.execute(niveauId)
                .stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * PUT /api/specialites/{id}
     * Met à jour une spécialité existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpecialiteResponseDTO> updateSpecialite(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSpecialiteRequestDTO request) {
        log.info("Requête de mise à jour de la spécialité id={}", id);
        Specialite specialite = updateSpecialiteUC.execute(id, request);
        return ResponseEntity.ok(SpecialiteResponseDTO.fromDomain(specialite));
    }

    /**
     * DELETE /api/specialites/{id}
     * Supprime définitivement une spécialité.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSpecialiteResponseDTO> deleteSpecialite(@PathVariable Long id) {
        log.info("Requête de suppression de la spécialité id={}", id);
        Specialite specialite = findSpecialiteByIdUC.execute(id);
        deleteSpecialiteUC.execute(id);
        return ResponseEntity.ok(DeleteSpecialiteResponseDTO.fromDomain(specialite));
    }

    /**
     * PATCH /api/specialites/{id}/activer
     * Active une spécialité désactivée.
     */
    @PatchMapping("/{id}/activer")
    public ResponseEntity<SpecialiteResponseDTO> activerSpecialite(@PathVariable Long id) {
        log.info("Requête d'activation de la spécialité id={}", id);
        Specialite specialite = toggleSpecialiteStatusUC.execute(id, true);
        return ResponseEntity.ok(SpecialiteResponseDTO.fromDomain(specialite));
    }

    /**
     * PATCH /api/specialites/{id}/desactiver
     * Désactive une spécialité (sans la supprimer).
     */
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<SpecialiteResponseDTO> desactiverSpecialite(@PathVariable Long id) {
        log.info("Requête de désactivation de la spécialité id={}", id);
        Specialite specialite = toggleSpecialiteStatusUC.execute(id, false);
        return ResponseEntity.ok(SpecialiteResponseDTO.fromDomain(specialite));
    }
}
