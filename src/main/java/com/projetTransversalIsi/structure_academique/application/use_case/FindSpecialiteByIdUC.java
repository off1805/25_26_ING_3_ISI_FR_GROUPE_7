package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindSpecialiteByIdUC {

    private final SpecialiteRepository specialiteRepository;

    public Specialite execute(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));
    }
}
