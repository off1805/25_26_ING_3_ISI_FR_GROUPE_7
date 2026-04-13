package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.exception.SchoolNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkCycleToSchoolUC {

    private final CycleRepository cycleRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public Cycle execute(Long cycleId, Long schoolId) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new CycleNotFoundException(cycleId));
        if (schoolId != null && !schoolRepository.existsById(schoolId)) {
            throw new SchoolNotFoundException(schoolId);
        }
        cycle.setSchoolId(schoolId);
        return cycleRepository.save(cycle);
    }
}
