package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.ModifyCycleStatusDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModifyCycleStatusUCImpl implements ModifyCycleStatusUC {

    private final CycleRepository cycleRepository;

    @Transactional
    @Override
    public Cycle execute(Long id, ModifyCycleStatusDTO statusDTO) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        cycle.setStatus(statusDTO.status());
        return cycleRepository.save(cycle);
    }
}
