package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.UpdateCycleRequestDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCycleUCImpl implements UpdateCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    @Override
    public Cycle execute(Long id, UpdateCycleRequestDTO request) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        cycle.setName(request.name());
        cycle.setDurationYears(request.durationYears());
        cycle.setDescription(request.description());
        return cycleRepository.save(cycle);
    }
}
