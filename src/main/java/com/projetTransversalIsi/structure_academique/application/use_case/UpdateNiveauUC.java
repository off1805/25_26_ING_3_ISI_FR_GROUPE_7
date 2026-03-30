package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateNiveauRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.exception.FiliereNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateNiveauUC {

    private final NiveauRepository niveauRepo;
    private final FiliereRepository filiereRepo;

    @Transactional
    public Niveau execute(UpdateNiveauRequestDTO request, Long id) {
        Niveau niveau = niveauRepo.findById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));

        Filiere filiere = filiereRepo.findById(request.filiereId())
                .orElseThrow(() -> new FiliereNotFoundException(request.filiereId()));

        niveau.update(request.ordre(), request.description(), filiere);
        return niveauRepo.save(niveau);
    }
}
