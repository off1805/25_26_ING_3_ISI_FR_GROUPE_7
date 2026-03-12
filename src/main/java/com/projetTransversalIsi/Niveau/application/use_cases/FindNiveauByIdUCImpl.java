package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import com.projetTransversalIsi.Niveau.domain.exceptions.NiveauNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindNiveauByIdUCImpl implements FindNiveauByIdUC {

    private final NiveauRepository niveauRepo;

    @Override
    public Niveau execute(Long id) {
        return niveauRepo.findActiveById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));
    }
}
