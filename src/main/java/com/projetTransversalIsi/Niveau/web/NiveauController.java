package com.projetTransversalIsi.Niveau.web;

import com.projetTransversalIsi.Niveau.application.dto.*;
import com.projetTransversalIsi.Niveau.application.services.DefaultNiveauService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/niveau")
@RequiredArgsConstructor
@Slf4j
public class NiveauController {

    private final DefaultNiveauService niveauService;

    @PostMapping
    public ResponseEntity<NiveauResponseDTO> createNiveau(@Valid @RequestBody CreateNiveauRequestDTO request) {
        log.info("Création d'un niveau: ordre {}, filiereId {}", request.ordre(), request.filiereId());
        return ResponseEntity.status(HttpStatus.CREATED).body(niveauService.createNiveau(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NiveauResponseDTO> updateNiveau(@PathVariable Long id, @Valid @RequestBody UpdateNiveauRequestDTO request) {
        if (!id.equals(request.id())) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Mise à jour du niveau ID: {}", id);
        return ResponseEntity.ok(niveauService.updateNiveau(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        log.info("Suppression du niveau ID: {}", id);
        niveauService.deleteNiveau(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NiveauResponseDTO> getNiveauById(@PathVariable Long id) {
        log.info("Récupération du niveau ID: {}", id);
        return ResponseEntity.ok(niveauService.getNiveauById(id));
    }

    @GetMapping
    public ResponseEntity<List<NiveauResponseDTO>> searchNiveaux(
            @RequestParam(required = false) Integer ordre,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        log.info("Recherche de niveaux: ordre {}, description {}", ordre, description);
        SearchNiveauRequestDTO criteria = new SearchNiveauRequestDTO(ordre, description, includeDeleted);
        return ResponseEntity.ok(niveauService.searchNiveaux(criteria));
    }
}
