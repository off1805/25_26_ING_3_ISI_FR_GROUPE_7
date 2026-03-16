package com.projetTransversalIsi.cycle.web;

import com.projetTransversalIsi.cycle.application.dto.*;
import com.projetTransversalIsi.cycle.application.use_cases.*;
import com.projetTransversalIsi.cycle.domain.Cycle;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cycle")
@RequiredArgsConstructor
@Slf4j
public class CycleController {

    private final CreateCycleUC createCycleUC;
    private final FindCycleByIdUC findCycleByIdUC;
    private final GetAllCyclesUC getAllCyclesUC;
    private final DeleteCycleUC deleteCycleUC;
    private final UpdateCycleUC updateCycleUC;
    private final ModifyCycleStatusUC modifyCycleStatusUC;

    @PostMapping("")
    public ResponseEntity<CycleResponseDTO> createCycle(@Valid @RequestBody CreateCycleRequestDTO request) {
        log.info("Requête de création de cycle reçue : code={}", request.code());
        Cycle cycle = createCycleUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CycleResponseDTO.fromDomain(cycle));
    }

    @GetMapping("")
    public ResponseEntity<List<CycleResponseDTO>> getAllCycles() {
        log.info("Requête de récupération de tous les cycles");
        List<CycleResponseDTO> cycles = getAllCyclesUC.execute().stream()
                .map(CycleResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cycles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CycleResponseDTO> getCycleById(@PathVariable("id") Long id) {
        log.info("Requête de récupération du cycle ID : {}", id);
        Cycle cycle = findCycleByIdUC.execute(id);
        return ResponseEntity.ok(CycleResponseDTO.fromDomain(cycle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CycleResponseDTO> updateCycle(@PathVariable("id") Long id,
                                                         @Valid @RequestBody UpdateCycleRequestDTO request) {
        log.info("Requête de mise à jour du cycle ID : {}", id);
        Cycle cycle = updateCycleUC.execute(id, request);
        return ResponseEntity.ok(CycleResponseDTO.fromDomain(cycle));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CycleResponseDTO> modifyStatus(@PathVariable("id") Long id,
                                                          @Valid @RequestBody ModifyCycleStatusDTO statusDTO) {
        log.info("Requête de modification de statut du cycle ID : {} vers {}", id, statusDTO.status());
        Cycle cycle = modifyCycleStatusUC.execute(id, statusDTO);
        return ResponseEntity.ok(CycleResponseDTO.fromDomain(cycle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCycleResponseDTO> deleteCycle(@PathVariable("id") Long id) {
        log.info("Requête de suppression du cycle ID : {}", id);
        DeleteCycleRequestDTO command = new DeleteCycleRequestDTO(id);
        deleteCycleUC.execute(command);
        Cycle deletedCycle = findCycleByIdUC.execute(id);
        return ResponseEntity.ok(DeleteCycleResponseDTO.fromDomain(deletedCycle));
    }
}
