package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateCycleRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCycleUC {

    private final CycleRepository cycleRepository;

    @Transactional
    public Cycle execute(CreateCycleRequestDTO request) {
        if (cycleRepository.existsByCode(request.code())) {
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
