package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindCycleByIdUCImpl implements FindCycleByIdUC {

    private final CycleRepository cycleRepository;

    @Override
    public Cycle execute(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
    }
}
