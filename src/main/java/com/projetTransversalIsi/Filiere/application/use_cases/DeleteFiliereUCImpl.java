package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;

import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteFiliereUCImpl implements DeleteFiliereUC {

    private final FiliereRepository filiereRepo;

    @Override
    @Transactional
    public void execute(Long id) {

        Filiere filiere = filiereRepo.findById(id)
                .orElseThrow(() -> new FiliereNotFoundException(id));

        if (filiere.isDeleted()) {
            throw new IllegalStateException("La filière " + id + " est déjà supprimée");
        }

        filiere.delete();
        filiereRepo.save(filiere);
    }
}
