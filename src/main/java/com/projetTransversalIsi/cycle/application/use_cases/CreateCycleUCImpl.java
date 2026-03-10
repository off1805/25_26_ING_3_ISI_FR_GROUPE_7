package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.CreateCycleRequestDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCycleUCImpl implements CreateCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    @Override
    public Cycle execute(CreateCycleRequestDTO request) {
        if (cycleRepository.cycleAlreadyExists(request.code())) {
            throw new CycleAlreadyExistsException(request.code());
        }
        Cycle cycle = new Cycle(
                request.name(),
                request.code().toUpperCase(),
                request.durationYears(),
                request.description()
        );
        return cycleRepository.save(cycle);
    }
}
