package com.projetTransversalIsi.cycle.application.services;

import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultCycleService implements CycleService {

    private final CycleRepository cycleRepository;

    @Override
    public Optional<Cycle> findCycleById(Long id) {
        return cycleRepository.findById(id);
    }

    @Override
    public List<Cycle> findAllActiveCycles() {
        return cycleRepository.findAllActive();
    }

    @Override
    public void deleteCycle(Long id) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        cycle.delete();
        cycleRepository.save(cycle);
    }
}
