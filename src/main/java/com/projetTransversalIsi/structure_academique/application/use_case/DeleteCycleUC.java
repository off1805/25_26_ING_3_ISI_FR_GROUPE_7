package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    public void execute(Long id) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new CycleNotFoundException(id));
        if (cycle.isDeleted()) {
            throw new IllegalStateException("Le cycle " + id + " est déjà supprimé.");
        }
        cycle.delete();
        cycleRepository.save(cycle);
    }
}
