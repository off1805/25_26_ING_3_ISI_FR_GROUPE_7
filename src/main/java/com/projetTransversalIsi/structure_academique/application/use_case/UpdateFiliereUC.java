package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateFiliereRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateFiliereUC {

    private final FiliereRepository filiereRepo;
    private final CycleRepository cycleRepo;

    @Transactional
    public Filiere execute(UpdateFiliereRequestDTO command, Long id) {
        Filiere filiere = filiereRepo.findById(id)
                .orElseThrow(() -> new FiliereNotFoundException(id));

        Cycle cycle = cycleRepo.findById(command.cycleId())
                .orElseThrow(() -> new CycleNotFoundException(command.cycleId()));

        filiere.update(command.nom(), command.description());
        filiere.setCycle(cycle);

        return filiereRepo.save(filiere);
    }
}
