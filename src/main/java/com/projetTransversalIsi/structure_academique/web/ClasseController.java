package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.service.ClasseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final ClasseService classeService;

    @PostMapping
    public ResponseEntity<ClasseResponseDTO> createClasse(@Valid @RequestBody CreateClassRequestDTO request) {
        return new ResponseEntity<>(classeService.createClasse(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> updateClasse(@PathVariable Long id, @Valid @RequestBody UpdateClasseRequestDTO request) {
        return ResponseEntity.ok(classeService.updateClasse(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ClasseResponseDTO>> getAllClasses() {
        return ResponseEntity.ok(classeService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseResponseDTO> getClasseById(@PathVariable Long id) {
        return ResponseEntity.ok(classeService.getClasseById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<List<ClasseResponseDTO>> getClassesBySpecialiteId(@PathVariable Long specialiteId) {
        return ResponseEntity.ok(classeService.getClassesBySpecialiteId(specialiteId));
    }
}
