package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.ModifyCycleStatusDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModifyCycleStatusUC {

    private final CycleRepository cycleRepository;

    @Transactional
    public Cycle execute(Long id, ModifyCycleStatusDTO statusDTO) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        cycle.setStatus(statusDTO.status());
        return cycleRepository.save(cycle);
    }
}
