package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.UpdateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateFiliereUCImpl implements UpdateFiliereUC {

    private final FiliereRepository filiereRepo;

    @Override
    @Transactional
    public Filiere execute(UpdateFiliereRequestDTO command) {

        Filiere filiere = filiereRepo.findById(command.id())
                .orElseThrow(() -> new FiliereNotFoundException(command.id()));

        filiere.update(
                command.nom(),
                command.description()
        );

        return filiereRepo.save(filiere);
    }
}
