package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.CreateFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateFiliereUCImpl implements CreateFiliereUC {

    private final FiliereRepository filiereRepo;

    @Override
    @Transactional
    public Filiere execute(CreateFiliereRequestDTO command) {

        if (filiereRepo.existsByCode(command.code())) {
            throw new FiliereAlreadyExistsException(command.code());
        }

        Filiere filiere = new Filiere(
                command.code(),
                command.nom(),
                command.description()
        );


        return filiereRepo.save(filiere);
    }
}