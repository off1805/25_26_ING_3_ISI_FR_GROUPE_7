package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.UpdateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.cycle.domain.CycleRepository;
import com.projetTransversalIsi.cycle.domain.exceptions.CycleNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateFiliereUCImpl implements UpdateFiliereUC {

    private final FiliereRepository filiereRepo;
    private final CycleRepository cycleRepo;

    @Override
    @Transactional
    public Filiere execute(UpdateFiliereRequestDTO command) {

        Filiere filiere = filiereRepo.findById(command.id())
                .orElseThrow(() -> new FiliereNotFoundException(command.id()));

        Cycle cycle = cycleRepo.findById(command.cycleId())
                .orElseThrow(() -> new CycleNotFoundException(command.cycleId()));

        filiere.update(
                command.nom(),
                command.description()
        );
        filiere.setCycle(cycle);

        return filiereRepo.save(filiere);
    }
}
