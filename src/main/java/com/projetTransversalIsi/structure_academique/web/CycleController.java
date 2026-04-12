package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.service.CycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cycle")
@RequiredArgsConstructor
@Slf4j
public class CycleController {

    private final CycleService cycleService;

    @PostMapping("")
    public ResponseEntity<CycleResponseDTO> createCycle(@Valid @RequestBody CreateCycleRequestDTO request) {
        log.info("Requête de création de cycle reçue : code={}", request.code());
        return ResponseEntity.status(HttpStatus.CREATED).body(cycleService.createCycle(request));
    }

    @GetMapping("")
    public ResponseEntity<List<CycleResponseDTO>> getAllCycles() {
        log.info("Requête de récupération de tous les cycles");
        return ResponseEntity.ok(cycleService.getAllCycles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CycleResponseDTO> getCycleById(@PathVariable("id") Long id) {
        log.info("Requête de récupération du cycle ID : {}", id);
        return ResponseEntity.ok(cycleService.getCycleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CycleResponseDTO> updateCycle(@PathVariable("id") Long id,
                                                         @Valid @RequestBody UpdateCycleRequestDTO request) {
        log.info("Requête de mise à jour du cycle ID : {}", id);
        return ResponseEntity.ok(cycleService.updateCycle(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CycleResponseDTO> modifyStatus(@PathVariable("id") Long id,
                                                          @Valid @RequestBody ModifyCycleStatusDTO statusDTO) {
        log.info("Requête de modification de statut du cycle ID : {} vers {}", id, statusDTO.status());
        return ResponseEntity.ok(cycleService.modifyStatus(id, statusDTO));
    }

    @PutMapping("/{id}/school")
    public ResponseEntity<CycleResponseDTO> linkCycleToSchool(
            @PathVariable("id") Long id,
            @RequestBody LinkCycleToSchoolRequestDTO request) {
        log.info("Rattachement du cycle ID : {} à l'école ID : {}", id, request.schoolId());
        return ResponseEntity.ok(cycleService.linkCycleToSchool(id, request.schoolId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCycleResponseDTO> deleteCycle(@PathVariable("id") Long id) {
        log.info("Requête de suppression du cycle ID : {}", id);
        CycleResponseDTO cycleBeforeDeletion = cycleService.getCycleById(id);
        cycleService.deleteCycle(id);
        
        // Convert to DeleteCycleResponseDTO manually as we only have CycleResponseDTO here
        DeleteCycleResponseDTO response = new DeleteCycleResponseDTO(
                cycleBeforeDeletion.id(),
                cycleBeforeDeletion.name(),
                cycleBeforeDeletion.code(),
                java.time.LocalDateTime.now() // Close enough to the actual deletion time
        );
        return ResponseEntity.ok(response);
    }
}
