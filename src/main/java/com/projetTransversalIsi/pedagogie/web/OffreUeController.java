package com.projetTransversalIsi.pedagogie.web;

import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.OffreUeResponseDTO;
import com.projetTransversalIsi.pedagogie.application.use_cases.*;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.infrastructure.OffreUeMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offre-ue")
@RequiredArgsConstructor
@Slf4j
public class OffreUeController {

    private final CreateOffreUeUC createOffreUeUC;
    private final FindOffreUeByIdUC findOffreUeByIdUC;
    private final DeleteOffreUeUC deleteOffreUeUC;
    private final SearchOffreUeUC searchOffreUeUC;
    private final OffreUeMapper offreUeMapper;

    @PostMapping
    public ResponseEntity<OffreUeResponseDTO> createOffreUe(
            @Valid @RequestBody CreateOffreUeRequestDTO request) {
        log.info("Création d'une offre UE : ueId={}, anneeScolaireId={}",
                request.ueId(), request.anneeScolaireId());
        OffreUe offreUe = createOffreUeUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offreUeMapper.toResponseDTO(offreUe));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreUeResponseDTO> getOffreUeById(@PathVariable Long id) {
        log.info("Récupération de l'offre UE id={}", id);
        OffreUe offreUe = findOffreUeByIdUC.execute(id);
        return ResponseEntity.ok(offreUeMapper.toResponseDTO(offreUe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffreUe(@PathVariable Long id) {
        log.info("Suppression de l'offre UE id={}", id);
        deleteOffreUeUC.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<OffreUeResponseDTO>> searchOffreUes(
            @RequestParam(required = false) Long anneeScolaireId,
            @RequestParam(required = false) Long ueId,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Recherche des offres UE : anneeScolaireId={}, ueId={}", anneeScolaireId, ueId);
        Page<OffreUe> resultats = searchOffreUeUC.execute(anneeScolaireId, ueId, pageable);
        return ResponseEntity.ok(resultats.map(offreUeMapper::toResponseDTO));
    }
}
