package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindCycleByIdUC {

    private final CycleRepository cycleRepository;

    public Cycle execute(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
    }
}
