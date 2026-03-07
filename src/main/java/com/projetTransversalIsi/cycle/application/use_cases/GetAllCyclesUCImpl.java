package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllCyclesUCImpl implements GetAllCyclesUC {

    private final CycleRepository cycleRepository;

    @Override
    public List<Cycle> execute() {
        return cycleRepository.findAll();
    }
}
