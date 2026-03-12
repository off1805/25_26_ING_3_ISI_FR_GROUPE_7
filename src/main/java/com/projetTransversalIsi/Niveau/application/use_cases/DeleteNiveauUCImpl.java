package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import com.projetTransversalIsi.Niveau.domain.exceptions.NiveauNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteNiveauUCImpl implements DeleteNiveauUC {

    private final NiveauRepository niveauRepo;

    @Override
    @Transactional
    public void execute(Long id) {
        Niveau niveau = niveauRepo.findById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));
        niveau.delete();
        niveauRepo.save(niveau);
    }
}
