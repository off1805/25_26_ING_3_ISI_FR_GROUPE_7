package com.projetTransversalIsi.classe.web;

import com.projetTransversalIsi.classe.application.dto.ClasseResponseDTO;
import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.application.dto.UpdateClasseRequestDTO;
import com.projetTransversalIsi.classe.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final CreateClassUC createClassUC;
    private final UpdateClasseUC updateClasseUC;
    private final FindAllClassesUC findAllClassesUC;
    private final FindClasseByIdUC findClasseByIdUC;
    private final DeleteClasseUC deleteClasseUC;

    @PostMapping
    public ResponseEntity<ClasseResponseDTO> createClasse(@Valid @RequestBody CreateClassRequestDTO request) {
        return new ResponseEntity<>(ClasseResponseDTO.fromDomain(createClassUC.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> updateClasse(@PathVariable Long id, @Valid @RequestBody UpdateClasseRequestDTO request) {
        return ResponseEntity.ok(ClasseResponseDTO.fromDomain(updateClasseUC.execute(id, request)));
    }

    @GetMapping
    public ResponseEntity<List<ClasseResponseDTO>> getAllClasses() {
        return ResponseEntity.ok(findAllClassesUC.execute().stream()
                .map(ClasseResponseDTO::fromDomain)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> getClasseById(@PathVariable Long id) {
        return ResponseEntity.ok(ClasseResponseDTO.fromDomain(findClasseByIdUC.execute(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        deleteClasseUC.execute(id);
        return ResponseEntity.noContent().build();
    }
}
