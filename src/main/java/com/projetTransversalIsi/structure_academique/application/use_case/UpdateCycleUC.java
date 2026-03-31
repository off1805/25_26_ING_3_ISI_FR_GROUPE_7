package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateCycleRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    public Cycle execute(Long id, UpdateCycleRequestDTO request) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        cycle.setName(request.name());
        cycle.setDurationYears(request.durationYears());
        cycle.setDescription(request.description());
        return cycleRepository.save(cycle);
    }
}
