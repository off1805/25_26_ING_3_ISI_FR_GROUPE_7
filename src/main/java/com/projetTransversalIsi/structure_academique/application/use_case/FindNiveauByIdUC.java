package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindNiveauByIdUC {

    private final NiveauRepository niveauRepo;

    public Niveau execute(Long id) {
        return niveauRepo.findActiveById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));
    }
}
