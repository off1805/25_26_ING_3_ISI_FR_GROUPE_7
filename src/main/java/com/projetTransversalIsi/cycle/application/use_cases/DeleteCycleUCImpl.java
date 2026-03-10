package com.projetTransversalIsi.cycle.application.use_cases;

import com.projetTransversalIsi.cycle.application.dto.DeleteCycleRequestDTO;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteCycleUCImpl implements DeleteCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    @Override
    public void execute(DeleteCycleRequestDTO request) {
        Cycle cycle = cycleRepository.findById(request.id())
                .orElseThrow(() -> new CycleNotFoundException(request.id()));
        if (cycle.isDeleted()) {
            throw new IllegalStateException("Le cycle " + request.id() + " est déjà supprimé.");
        }
        cycle.delete();
        cycleRepository.save(cycle);
    }
}
