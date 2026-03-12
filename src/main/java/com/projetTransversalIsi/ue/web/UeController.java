package com.projetTransversalIsi.ue.web;

import com.projetTransversalIsi.ue.application.dto.*;
import com.projetTransversalIsi.ue.application.use_cases.*;
import com.projetTransversalIsi.ue.domain.Ue;
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
@RequestMapping("/api/ue")
@RequiredArgsConstructor
@Slf4j
public class UeController {

    private final CreateUeUC createUeUC;
    private final FindUeByIdUC findUeByIdUC;
    private final UpdateUeUC updateUeUC;
    private final DeleteUeUC deleteUeUC;
    private final SearchUeUC searchUeUC;
    private final com.projetTransversalIsi.ue.infrastructure.UeMapper ueMapper;

    @PostMapping
    public ResponseEntity<UeResponseDTO> createUe(@Valid @RequestBody CreateUeRequestDTO request) {
        log.info("Requête de création d'UE reçue : {}", request.code());
        Ue ue = createUeUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ueMapper.toResponseDTO(ue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UeResponseDTO> getUeById(@PathVariable("id") Long id) {
        log.info("Requête de récupération d'UE pour l'ID : {}", id);
        Ue ue = findUeByIdUC.execute(id);
        return ResponseEntity.ok(ueMapper.toResponseDTO(ue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UeResponseDTO> updateUe(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateUeRequestDTO request) {
        log.info("Requête de mise à jour d'UE pour l'ID : {}", id);
        Ue ue = updateUeUC.execute(id, request);
        return ResponseEntity.ok(ueMapper.toResponseDTO(ue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UeResponseDTO> deleteUe(@PathVariable("id") Long id) {
        log.info("Requête de suppression d'UE pour l'ID : {}", id);
        deleteUeUC.execute(id);
        Ue ue = findUeByIdUC.execute(id);
        return ResponseEntity.ok(ueMapper.toResponseDTO(ue));
    }

    @GetMapping
    public ResponseEntity<Page<UeResponseDTO>> searchUes(
            @ModelAttribute UeFiltreDto command,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Ue> resultats = searchUeUC.execute(command, pageable);
        return ResponseEntity.ok(resultats.map(ueMapper::toResponseDTO));
    }
}
