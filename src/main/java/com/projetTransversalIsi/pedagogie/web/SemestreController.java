package com.projetTransversalIsi.pedagogie.web;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;
import com.projetTransversalIsi.pedagogie.application.services.SemestreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semestres")
@RequiredArgsConstructor
public class SemestreController {

    private final SemestreService semestreService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateSemestreRequestDTO request) {
        try {
            SemestreResponseDTO response = semestreService.createSemestre(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SemestreResponseDTO>> findByAnneeAndNiveau(
            @RequestParam Long anneeScolaireId,
            @RequestParam Long niveauId
    ) {
        return ResponseEntity.ok(
                semestreService.getSemestresByAnneeScolaireAndNiveau(anneeScolaireId, niveauId)
        );
    }
}