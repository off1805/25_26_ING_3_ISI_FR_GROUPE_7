package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindFiliereByIdUCImpl implements FindFiliereByIdUC {

    private final FiliereRepository filiereRepo;

    @Override
    public Filiere execute(Long id) {
        return filiereRepo.findById(id)
                .orElseThrow(() -> new FiliereNotFoundException(id));
    }
}