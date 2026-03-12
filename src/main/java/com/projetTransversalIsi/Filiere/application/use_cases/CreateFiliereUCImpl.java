package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.CreateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereAlreadyExistsException;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateFiliereUCImpl implements CreateFiliereUC {

    private final FiliereRepository filiereRepo;
    private final CycleRepository cycleRepo;

    @Override
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