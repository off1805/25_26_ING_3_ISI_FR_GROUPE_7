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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/annee-scolaire")
public class AnneeScolaireControlleur {
    final private AnneeScolaireService anneeScolaireService;
    final private AnneeScolaireMapper anneeMapper;

    @PostMapping
    ResponseEntity<CreateAnneeScolaireResponseDTO> createAnnee(@Valid @RequestBody CreateAnneeScolaireRequestDTO command){
        AnneeScolaire newAnnee= anneeScolaireService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(anneeMapper.toResponseDto(newAnnee));
    }

}
