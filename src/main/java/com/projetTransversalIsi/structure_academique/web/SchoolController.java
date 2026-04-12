package com.projetTransversalIsi.structure_academique.web;

import com.projetTransversalIsi.structure_academique.application.dto.CreateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.application.dto.SchoolResponseDTO;
import com.projetTransversalIsi.structure_academique.application.dto.UpdateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.application.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    public ResponseEntity<SchoolResponseDTO> createSchool(@Valid @RequestBody CreateSchoolRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.createSchool(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponseDTO> getSchoolById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolResponseDTO> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSchoolRequestDTO request) {
        return ResponseEntity.ok(schoolService.updateSchool(id, request));
    }
}
