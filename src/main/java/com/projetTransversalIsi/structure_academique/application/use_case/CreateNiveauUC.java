package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateNiveauRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauAlreadyExistsException;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNiveauUC {

    private final NiveauRepository niveauRepo;
    private final FiliereRepository filiereRepo;

    @Transactional
    public Niveau execute(CreateNiveauRequestDTO request) {
        if (niveauRepo.existsByOrdreAndFiliereIdAndDeletedFalse(request.ordre(), request.filiereId())) {
            throw new NiveauAlreadyExistsException(request.ordre(), request.filiereId());
        }

        Filiere filiere = filiereRepo.findById(request.filiereId())
                .orElseThrow(() -> new FiliereNotFoundException(request.filiereId()));

        Niveau niveau = new Niveau(request.ordre(), request.description(), filiere);
        return niveauRepo.save(niveau);
    }
}
