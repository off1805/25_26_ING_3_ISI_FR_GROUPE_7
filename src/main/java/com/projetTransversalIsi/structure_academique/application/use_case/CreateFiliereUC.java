package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateFiliereRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.model.Cycle;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.CycleRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereAlreadyExistsException;
import com.projetTransversalIsi.structure_academique.domain.exception.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateFiliereUC {

    private final FiliereRepository filiereRepo;
    private final CycleRepository cycleRepo;

    @Transactional
    public Filiere execute(CreateFiliereRequestDTO command) {
        if (filiereRepo.existsByCode(command.code())) {
            throw new FiliereAlreadyExistsException(command.code());
        }

        Cycle cycle = cycleRepo.findById(command.cycleId())
                .orElseThrow(() -> new CycleNotFoundException(command.cycleId()));

        Filiere filiere = new Filiere(
                command.code(),
                command.nom(),
                command.description()
        );
        filiere.setCycle(cycle);

        return filiereRepo.save(filiere);
    }
}
