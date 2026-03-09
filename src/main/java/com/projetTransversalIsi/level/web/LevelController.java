package com.projetTransversalIsi.level.web;

import com.projetTransversalIsi.level.application.dto.LevelResponseDTO;
import com.projetTransversalIsi.level.application.dto.CreateLevelRequestDTO;
import com.projetTransversalIsi.level.application.usecase.CreateLevelUC;
import com.projetTransversalIsi.level.domain.level;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
@Slf4j
public class LevelController {
    private final CreateLevelUC createLevelUC;

    @PostMapping
    public ResponseEntity<LevelResponseDTO> createLevel(@Valid @RequestBody CreateLevelRequestDTO request) {
        log.info("Requête de création de level reçue : {}", request.nom());
        level level = createLevelUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LevelResponseDTO.fromDomain(level));
    }
}
