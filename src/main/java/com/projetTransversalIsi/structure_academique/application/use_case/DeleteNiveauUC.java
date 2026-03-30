package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteNiveauUC {

    private final NiveauRepository niveauRepo;

    @Transactional
    public void execute(Long id) {
        Niveau niveau = niveauRepo.findById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));
        niveau.delete();
        niveauRepo.save(niveau);
    }
}
