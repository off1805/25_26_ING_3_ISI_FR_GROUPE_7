package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindFiliereByIdUC {

    private final FiliereRepository filiereRepo;

    public Filiere execute(Long id) {
        return filiereRepo.findById(id)
                .orElseThrow(() -> new FiliereNotFoundException(id));
    }
}
