package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateCycleRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleAlreadyExistsException;
import com.projetTransversalIsi.structure_academique.domain.exception.SchoolNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCycleUC {

    private final CycleRepository cycleRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public Cycle execute(CreateCycleRequestDTO request) {
        if (cycleRepository.existsByCode(request.code())) {
            throw new CycleAlreadyExistsException(request.code());
        }
        if (request.schoolId() != null && !schoolRepository.existsById(request.schoolId())) {
            throw new SchoolNotFoundException(request.schoolId());
        }
        Cycle cycle = new Cycle(
                request.name(),
                request.code().toUpperCase(),
                request.durationYears(),
                request.description()
        );
        cycle.setSchoolId(request.schoolId());
        return cycleRepository.save(cycle);
    }
}
