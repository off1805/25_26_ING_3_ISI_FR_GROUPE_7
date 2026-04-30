package com.projetTransversalIsi.pedagogie.web;

import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireResponseDTO;
import com.projetTransversalIsi.pedagogie.application.services.AnneeScolaireService;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/annee-scolaire")
public class AnneeScolaireControlleur {
    final private AnneeScolaireService anneeScolaireService;
    final private AnneeScolaireMapper anneeMapper;

    @GetMapping
    ResponseEntity<List<CreateAnneeScolaireResponseDTO>> getAllAnnees() {
        List<CreateAnneeScolaireResponseDTO> result = anneeScolaireService.getAll()
                .stream().map(anneeMapper::toResponseDto).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<CreateAnneeScolaireResponseDTO> createAnnee(@Valid @RequestBody CreateAnneeScolaireRequestDTO command) {
        AnneeScolaire newAnnee = anneeScolaireService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(anneeMapper.toResponseDto(newAnnee));
    }

    @PatchMapping("/{id}/activate")
    ResponseEntity<CreateAnneeScolaireResponseDTO> activateAnnee(@PathVariable Long id) {
        AnneeScolaire updated = anneeScolaireService.activate(id);
        return ResponseEntity.ok(anneeMapper.toResponseDto(updated));
    }
}
