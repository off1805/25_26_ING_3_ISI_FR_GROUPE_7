package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import com.projetTransversalIsi.Filiere.domain.exceptions.FiliereNotFoundException;
import com.projetTransversalIsi.Niveau.application.dto.UpdateNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import com.projetTransversalIsi.Niveau.domain.exceptions.NiveauNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateNiveauUCImpl implements UpdateNiveauUC {

    private final NiveauRepository niveauRepo;
    private final FiliereRepository filiereRepo;

    @Override
    @Transactional
    public Niveau execute(UpdateNiveauRequestDTO request,Long id) {
        Niveau niveau = niveauRepo.findById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));

        Filiere filiere = filiereRepo.findById(request.filiereId())
                .orElseThrow(() -> new FiliereNotFoundException(request.filiereId()));

        niveau.update(request.ordre(), request.description(), filiere);
        return niveauRepo.save(niveau);
    }
}
