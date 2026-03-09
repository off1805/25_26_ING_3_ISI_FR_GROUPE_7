package com.projetTransversalIsi.classe.web;

import com.projetTransversalIsi.classe.application.dto.ClasseResponseDTO;
import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.application.usecase.CreateClassUC;
import com.projetTransversalIsi.classe.domain.classe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Slf4j
public class ClasseController {
    private final CreateClassUC createClassUC;

    @PostMapping
    public ResponseEntity<ClasseResponseDTO> createClasse(@Valid @RequestBody CreateClassRequestDTO request) {
        log.info("Requête de création de classe reçue : {}", request.Classname());
        classe classe = createClassUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClasseResponseDTO.fromDomain(classe));
    }
}

