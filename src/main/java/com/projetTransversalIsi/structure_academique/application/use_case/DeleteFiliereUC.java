package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteFiliereUC {

    private final FiliereRepository filiereRepo;

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
