package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import com.projetTransversalIsi.Niveau.application.dto.CreateNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import com.projetTransversalIsi.Niveau.domain.exceptions.NiveauAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNiveauUCImpl implements CreateNiveauUC {

    private final NiveauRepository niveauRepo;
    private final FiliereRepository filiereRepo;

    @Override
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
